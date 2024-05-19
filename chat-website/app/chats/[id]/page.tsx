import {Textarea} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import {Card, CardBody, CardHeader} from "@nextui-org/card";
import {Metadata} from "next";

export const metadata: Metadata = {
	title: 'Chat',
}

export default function ChatPage({ params }: { params: { id: string } }) {
	const username = "User";
	const messages = [
		{
			id: 1,
			sender: "John",
			text: "Hi guys! How are you doing today?"
		},
		{
			id: 2,
			sender: "User",
			text: "It's working!"
		},
		{
			id: 3,
			sender: "Damon",
			text: "Woo-hoo!"
		},
	]

	return (
		<div className="flex flex-col w-full h-full">
			<h2 className="text-center text-2xl font-bold leading-9 tracking-tight">Chat {params.id}</h2>
			<div className="p-4 flex flex-col-reverse gap-y-2 flex-nowrap flex-grow overflow-scroll h-96">
				{messages.reverse().map(message =>
					<Card key={message.id} className={"max-w-96 shrink-0 " + (message.sender !== username ? "self-start" : "self-end")} shadow="sm">
						{ message.sender !== username && (<CardHeader className="text-xs pb-0">{message.sender}</CardHeader>)}
						<CardBody>
							<p>{message.text}</p>
						</CardBody>
					</Card>
				)}
			</div>
			<div className="flex flex-row gap-x-2 items-end mb-4">
				<Textarea
					placeholder="Write a message"
					className="flex-auto"
					minRows={1}
				/>
				<Button>Send</Button>
			</div>
		</div>
	);
}
