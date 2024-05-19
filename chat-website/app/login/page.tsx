import {Input} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import NextLink from "next/link";
import {Metadata} from "next";

export const metadata: Metadata = {
	title: 'Log In',
}

export default function LoginPage() {
	return (
		<div className="flex min-h-full flex-col justify-center w-72">
			<h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight">Log in to your account</h2>

			<div className="mt-6 sm:mx-auto sm:w-full sm:max-w-sm">
				<form className="space-y-4" action="#" method="POST">
					<Input type="text" label="Username"/>
					<Input type="password" label="Password"/>

					<Button type="submit" className="w-full">Log in</Button>
				</form>

				<p className="mt-4 text-center text-sm text-gray-500">
					Not a member? <NextLink href="/signup" className="text-sm text-blue-600">Sign up</NextLink>
				</p>
			</div>
		</div>
	);
}
