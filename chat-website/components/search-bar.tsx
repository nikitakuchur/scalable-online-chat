'use client'

import {Input} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import {FormEvent} from "react";

export interface SearchBarProps {
    defaultValue: string,
    onSearch: (value: string) => void,
}

export default function SearchBar(props: SearchBarProps) {
    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        const formData = new FormData(event.currentTarget)
        const searchPhrase = formData.get("searchPhrase");

        props.onSearch(searchPhrase?.toString() ?? "");
    }

    return (
        <form className="flex flex-row gap-2 w-5/6 self-center pb-4" onSubmit={handleSubmit}>
            <Input type="text" defaultValue={props.defaultValue} placeholder="Search for..." name="searchPhrase"></Input>
            <Button variant="bordered" type="submit">Search</Button>
        </form>
    );
}
