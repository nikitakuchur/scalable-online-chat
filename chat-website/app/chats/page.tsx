import {Metadata} from "next";
import ChatList, {Chat} from "@/components/chat-list";

export const metadata: Metadata = {
	title: 'Chats',
}

export default function ChatListPage() {
	const allChats : Chat[] = [
		{
			id: "1",
			owner: "User",
			name: "Programming chat",
			description: "A chat dedicated to programming"
		},
		{
			id: "2",
			owner: "Damon",
			name: "Blur fans",
			description: "Blur is the best band!",
		},
		{
			id: "3",
			owner: "Liam",
			name: "Oasis fans",
			description: "Oasis is the best band!"
		},
		{
			id: "4",
			owner: "User",
			name: "Cooking chat",
			description: "Let's cook together!"
		}
	];

	const myChats: Chat[] = [
		{
			id: "1",
			owner: "User",
			name: "Programming chat",
			description: "A chat dedicated to programming"
		},
		{
			id: "4",
			owner: "User",
			name: "Cooking chat",
			description: "Let's cook together!"
		}
	];

	return (
		<ChatList allChats={allChats} myChats={myChats}/>
	);
}
