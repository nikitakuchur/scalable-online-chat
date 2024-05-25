'use client'

import {Textarea} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import {Card, CardBody, CardHeader} from "@nextui-org/card";
import {FormEvent, useContext, useEffect, useRef, useState} from "react";
import {Client} from "@stomp/stompjs";
import {AuthContext} from "@/app/providers";
import {useParams} from "next/navigation";
import {jwtFetch, retrieveTokens} from "@/utils/jwt-fetch";
import {Spinner} from "@nextui-org/spinner";
import moment, {now} from "moment";

interface Message {
	id?: string,
	sender?: string,
	text: string,
	timestamp?: string,
	type?: string,
}

export default function ChatPage() {
	const params = useParams<{ id: string; }>()
	const { user} = useContext(AuthContext);

	const notificationRef = useRef<HTMLAudioElement>();
	const [ chatName, setChatName ] = useState<string>("");

	const [ loading, setLoading ] = useState<boolean>(true);
	const [ text, setText ] = useState<string>("");

	const clientRef = useRef<Client>();
	const [messages, setMessages] = useState<Message[]>([]);

	async function retrieveChatInfo() {
		const response = await jwtFetch(`/api/chats/${params.id}`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
			}
		});
		if (response.ok) {
			const chat: Chat = await response.json();
			setChatName(chat.name);
		} else {
			console.error("An error occurred while retrieving the chat info.")
		}
	}

	function playNotification() {
		const notification = notificationRef.current;
		if (!notification) {
			return;
		}
		if (notification.paused) {
			notification.play();
		} else {
			notification.currentTime = 0
		}
	}

	function establishConnection() {
		if (clientRef.current != null) {
			return () => {};
		}

		const client = new Client({
			brokerURL: process.env.NEXT_PUBLIC_WS_API_URL,
			beforeConnect: () => {
				const accessToken = localStorage.getItem("accessToken");
				client.connectHeaders = {
					Authorization: `Bearer ${accessToken}`,
				}
			},
			onConnect: () => {
				console.log("Connected!");
				setLoading((value) => false);
				client.subscribe(`/topic/${params.id}`, message => {
					const newMessage: Message = JSON.parse(message.body);
					setMessages((messages) => [newMessage, ...messages]);
					if (newMessage.type == "USER_MESSAGE") {
						playNotification();
					}
				});
			},
			onStompError: frame => {
				console.error("An error occurred while connecting to the chat.");
				retrieveTokens();
			}
		});

		client.activate();
		clientRef.current = client;
	}

	useEffect(() => {
		notificationRef.current = new Audio("/notification.mp3");

		retrieveChatInfo()
			.then(() => establishConnection());

		return () => {
			if (clientRef.current != null) {
				clientRef.current?.unsubscribe(`/topic/${params.id}`);
				clientRef.current?.deactivate();
			}
		}
	}, []);

	function handleSubmit(event: FormEvent<HTMLFormElement>) {
		event.preventDefault();

		if (user == null || text.trim() == "") {
			return;
		}

		const message: Message = {
			text: text,
		};
		clientRef.current?.publish({ destination: `/app/${params.id}`, body: JSON.stringify(message) });
		setText("");
	}

	function calculateTimestamp(message: Message) {
		const timestamp = moment(message.timestamp!);
		if (timestamp.isSame(now(), 'day')) {
			return timestamp.format("hh:mm");
		}
		return timestamp.format("DD.MM.YYYY hh:mm");
	}

	function renderUserMessage(message: Message) {
		return (
			<Card key={message.id} className={"max-w-96 shrink-0 " + (message.sender !== user?.sub ? "self-start" : "self-end")} shadow="sm">
				{ message.sender !== user?.sub && (<CardHeader className="text-xs pb-0">{message.sender}</CardHeader>)}
				<CardBody className="flex flex-col gap-x-4 items-end">
					<p>{message.text}</p>
					<div className="text-xs pt-0 text-gray-400">{calculateTimestamp(message)}</div>
				</CardBody>
			</Card>
		);
	}

	function renderServiceMessage(message: Message) {
		return (
			<div key={message.id} className="place-self-center text-gray-500 text-sm">{message.text}</div>
		);
	}

	return (
		<div className="flex flex-col w-full h-full">
			<h2 className="text-center text-2xl font-bold leading-9 tracking-tight">{chatName}</h2>
			<div className="p-4 flex flex-col-reverse gap-y-2 flex-nowrap flex-grow overflow-scroll h-96">
				{messages.map(message =>
					message.type == "USER_MESSAGE" ? renderUserMessage(message) : renderServiceMessage(message)
				)}
			</div>
			{ loading && <Spinner className="z-50 absolute inset-0" size="lg" /> }
			<form className="flex flex-row gap-x-2 items-end mb-4" onSubmit={handleSubmit}>
				<Textarea
					readOnly={loading}
					placeholder="Write a message"
					className="flex-auto"
					minRows={1}
					name="text"
					value={text}
					onChange={event => setText(event.target.value)}
				/>
				<Button type="submit">Send</Button>
			</form>
		</div>
	);
}
