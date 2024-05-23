'use client'

import {Input} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import NextLink from "next/link";
import {FormEvent, useState} from "react";
import {useRouter} from "next/navigation";
import * as React from "react";

interface Errors {
	email?: string;
	username?: string;
	password?: string;
}

export default function SignupPage() {
	const router = useRouter()

	const [errors , setErrors] = useState<Errors>({});
	const [formError , setFormError] = useState<string | null>(null);

	function clearErrors() {
		setErrors({});
		setFormError(null);
	}

	function isInvalid(field?: string): boolean {
		return formError != null || field != null;
	}

	async function handleSubmit(event: FormEvent<HTMLFormElement>) {
		event.preventDefault()

		const formData = new FormData(event.currentTarget)
		const email = formData.get('email');
		const username = formData.get('username');
		const password = formData.get('password');

		const response = await fetch('/api/signup', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ email, username, password }),
		})

		if (response.ok) {
			router.push("/login");
			return;
		}
		if (response.status === 400) {
			const errors = await response.json();
			setErrors(errors);
		} else {
			setFormError(await response.text());
		}
	}

	return (
		<div className="flex min-h-full flex-col justify-center w-72">
			<h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight">Create a new account</h2>

			<div className="mt-6 sm:mx-auto sm:w-full sm:max-w-sm">
				<form className="space-y-4" onSubmit={handleSubmit} onChange={() => clearErrors()}>
					<Input type="text" label="Email" name="email"
						   isInvalid={isInvalid(errors.email)} errorMessage={errors.email}/>
					<Input type="text" label="Username" name="username"
						   isInvalid={isInvalid(errors.username)} errorMessage={errors.username}/>
					<Input type="password" label="Password" name="password"
						   isInvalid={isInvalid(errors.password)} errorMessage={errors.password}/>
					{formError && <p className="text-tiny text-danger" >{formError}</p>}
					<Button type="submit" className="w-full">Sign up</Button>
				</form>

				<p className="mt-4 text-center text-sm text-gray-500">
					Have an account? <NextLink href="/login" className="text-sm text-blue-600">Log in</NextLink>
				</p>
			</div>
		</div>
	);
}
