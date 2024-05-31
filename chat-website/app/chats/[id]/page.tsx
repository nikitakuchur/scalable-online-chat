'use client'

import {Textarea} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import {FormEvent, useEffect, useRef, useState} from "react";
import {Client} from "@stomp/stompjs";
import {useParams} from "next/navigation";
import {jwtFetch, retrieveTokens} from "@/utils/jwt-fetch";
import {Spinner} from "@nextui-org/spinner";
import moment, {Moment} from "moment";
import MessageList, {Message} from "@/components/message-list";

export default function ChatPage() {
	const params = useParams<{ id: string; }>()

	const notificationRef = useRef<HTMLAudioElement>();
	const [ chatName, setChatName ] = useState<string>("");

	const [ loading, setLoading ] = useState<boolean>(true);
	const [ text, setText ] = useState<string>("");

	const clientRef = useRef<Client>();

	const [messages, setMessages] = useState<Message[]>([]);

	async function retrieveChatInfo() {
		const response = await jwtFetch(`/api/chats/${params.id}`, { method: 'GET' });
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

	async function loadLastMessages() {
		const lastMessages = await loadMessageHistory(moment())
		setMessages(lastMessages);
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
			onConnect: async () => {
				console.log("Connected!");
				await loadLastMessages();
				setLoading(() => false);
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

	async function loadMessageHistory(startFrom: Moment): Promise<Message[]> {
		const searchParams = new URLSearchParams({
			"size": "20",
			"startFrom": startFrom.toISOString(),
			"sort": "timestamp,desc"
		});
		const url = `/api/chats/${params.id}/messages?` + searchParams;
		const response = await jwtFetch(url, { method: 'GET' });
		if (response.ok) {
			const page = await response.json();
			return page.content;
		}
		console.error("An error occurred while retrieving the chat history.");
		return [];
	}

	useEffect(() => {
		notificationRef.current = new Audio("/notification.mp3");

		retrieveChatInfo()
			.then(establishConnection);

		return () => {
			if (clientRef.current != null) {
				clientRef.current?.unsubscribe(`/topic/${params.id}`);
				clientRef.current?.deactivate();
			}
		}
	}, []);

	function handleSubmit(event: FormEvent<HTMLFormElement>) {
		event.preventDefault();

		if (text.trim() == "") {
			return;
		}

		clientRef.current?.publish({ destination: `/app/${params.id}`, body: JSON.stringify({ text }) });
		setText("");
	}

	return (
		<div className="flex flex-col w-full h-full">
			<h2 className="text-center text-2xl font-bold leading-9 tracking-tight">{chatName}</h2>
			<MessageList messages={messages} loadMore={lastTimestamp => {
				loadMessageHistory(moment(lastTimestamp))
					.then(newMessages => {
						setMessages(messages => [...messages, ...newMessages]);
					});
			}}/>
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
