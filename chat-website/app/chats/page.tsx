import {Metadata} from "next";
import ChatList from "@/components/chat-list";

export const metadata: Metadata = {
	title: 'Chats',
}

export default function ChatListPage() {
	return <ChatList/>;
}
