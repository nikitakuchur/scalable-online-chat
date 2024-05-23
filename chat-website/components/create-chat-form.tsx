'use client'

import {Input, Textarea} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import {FormEvent, useState} from "react";
import {useRouter} from "next/navigation";
import {jwtFetch} from "@/utils/jwt-fetch";
import * as React from "react";

interface Errors {
    name?: string;
    description?: string;
}

export default function CreateChatForm() {
    const router = useRouter();
    const [errors , setErrors] = useState<Errors>({});

    function clearErrors() {
        setErrors({});
    }

    function isInvalid(field?: string): boolean {
        return field != null;
    }

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()

        const formData = new FormData(event.currentTarget)
        const name = formData.get('name');
        const description = formData.get('description');

        const response = await jwtFetch('/api/chats', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, description }),
        })

        if (response.ok) {
            const chatId = await response.text();
            router.push(`/chats/${chatId}`);
            return;
        }
        if (response.status === 400) {
            const errors = await response.json();
            setErrors(errors);
        } else {
            console.error(await response.text());
        }
    }

    return (
        <form className="space-y-4 w-2/3 mt-4" onSubmit={handleSubmit} onChange={() => clearErrors()}>
            <Input type="text" label="Name" name="name"
                   isInvalid={isInvalid(errors.name)} errorMessage={errors.name}/>
            <Textarea type="text" label="Description" name="description"
                      isInvalid={isInvalid(errors.description)} errorMessage={errors.description}/>
            <Button type="submit" className="w-full">Create</Button>
        </form>
    );
}
