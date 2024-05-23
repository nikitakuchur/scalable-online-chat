'use client'

import {Input} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import NextLink from "next/link";
import {useRouter} from "next/navigation";
import {FormEvent, useContext, useState} from "react";
import * as React from "react";
import {AuthContext} from "@/app/providers";
import {jwtDecode} from "jwt-decode";

interface Errors {
	username?: string;
	password?: string;
}

export default function LoginPage() {
	const router = useRouter()
	const { setUser } = useContext(AuthContext);

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
		const username = formData.get('username');
		const password = formData.get('password');

		const response = await fetch('/api/login', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ username, password }),
		})

		if (response.ok) {
			const tokens = await response.json();
			localStorage.setItem("accessToken", tokens.accessToken);
			localStorage.setItem("refreshToken", tokens.refreshToken);
			setUser(jwtDecode(tokens.accessToken));
			router.push("/");
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
			<h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight">Log in to your account</h2>

			<div className="mt-6 sm:mx-auto sm:w-full sm:max-w-sm">
				<form className="space-y-4" onSubmit={handleSubmit} onChange={() => clearErrors()}>
					<Input type="text" label="Username" name="username"
						   isInvalid={isInvalid(errors.username)} errorMessage={errors.username}/>
					<Input type="password" label="Password" name="password"
						   isInvalid={isInvalid(errors.password)} errorMessage={errors.password}/>
					{formError && <p className="text-tiny text-danger" >{formError}</p>}
					<Button type="submit" className="w-full">Log in</Button>
				</form>

				<p className="mt-4 text-center text-sm text-gray-500">
					Not a member? <NextLink href="/signup" className="text-sm text-blue-600">Sign up</NextLink>
				</p>
			</div>
		</div>
	);
}
