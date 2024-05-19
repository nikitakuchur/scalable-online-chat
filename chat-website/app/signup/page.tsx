import {Input} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import NextLink from "next/link";
import {Metadata} from "next";

export const metadata: Metadata = {
	title: 'Sign Up',
}

export default function LoginPage() {
	const username = "User";
	return (
		<div className="flex min-h-full flex-col justify-center w-72">
			<h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight">Create a new account</h2>

			<div className="mt-6 sm:mx-auto sm:w-full sm:max-w-sm">
				<form className="space-y-4" action="#" method="POST">
					<Input type="email" label="Email"/>
					<Input type="text" label="Username"/>
					<Input type="password" label="Password"/>

					<Button type="submit" className="w-full">Sign up</Button>
				</form>

				<p className="mt-4 text-center text-sm text-gray-500">
					Have an account? <NextLink href="/login" className="text-sm text-blue-600">Log in</NextLink>
				</p>
			</div>
		</div>
	);
}
