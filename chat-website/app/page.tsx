import {Button} from "@nextui-org/button";
import NextLink from "next/link";

export default function Home() {
	return (
		<section className="flex flex-col items-center justify-center h-5/6">
			<div className="flex flex-col items-center justify-center gap-8">
				<h2 className="text-center text-2xl font-bold leading-9 tracking-tight">Welcome to Online Chat!</h2>
				<NextLink href="/chats">
					<Button size="lg">Start chatting</Button>
				</NextLink>
			</div>
		</section>
	);
}
