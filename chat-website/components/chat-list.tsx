"use client";

import {Tab, Tabs} from "@nextui-org/tabs";
import {Card, CardBody, CardFooter, CardHeader} from "@nextui-org/card";
import NextLink from "next/link";
import {Pagination} from "@nextui-org/pagination";
import {Button} from "@nextui-org/button";
import {Input, Textarea} from "@nextui-org/input";
import {Modal, ModalBody, ModalContent, ModalFooter, ModalHeader, useDisclosure} from "@nextui-org/modal";
import {useState} from "react";

export interface Chat {
    id: string,
    owner: string,
    name: string,
    description: string,
}

interface ChatListProps {
    allChats: Chat[];
    myChats: Chat[];
}

export default function ChatList({ allChats, myChats }: ChatListProps) {
    const {isOpen, onOpen, onOpenChange} = useDisclosure();
    const [currentChat, setCurrentChat] = useState<Chat | null>(null);

    const username = "User";
    const toChatCard = (chat : Chat) => (
        <Card key={chat.id} className="shrink-0 w-5/6 self-center m-2" shadow="sm">
            <CardHeader className="pb-0 flex flex-row justify-between">
                <p>{chat.name}</p>
                {chat.owner === username && (
                    <NextLink href="#" onClick={() => {
                        setCurrentChat(chat);
                        onOpen();
                    }} className="text-red-600">Edit</NextLink>
                )}
            </CardHeader>
            <CardBody className="pb-0 text-gray-400">
                <p>{chat.description}</p>
            </CardBody>
            <CardFooter>
                <NextLink className="w-full text-right text-blue-600" href={`/chats/${chat.id}`}>Join</NextLink>
            </CardFooter>
        </Card>
    );

    const bar = (
        <div className="flex flex-row gap-2 w-5/6 self-center pb-4">
            <Input type="text" placeholder="Search for..."></Input>
            <Button variant="bordered">Search</Button>
        </div>
    );

    return (
        <div>
            <Tabs fullWidth aria-label="Options" placement="top">
                <Tab key="all-chats" title="All chats">
                    <div className="flex flex-nowrap flex-col mt-4 mb-4">
                        {bar}
                        {allChats.map(toChatCard)}
                        <Pagination className="self-center mt-4" total={10} initialPage={1} />
                    </div>
                </Tab>
                <Tab key="my-chats" title="My chats">
                    <div className="flex flex-nowrap flex-col mt-4 mb-4">
                        {bar}
                        {myChats.map(toChatCard)}
                        <Pagination className="self-center mt-4" total={10} initialPage={1} />
                    </div>
                </Tab>
                <Tab key="create-chat" title="Create new chat" className="flex justify-center">
                    <form className="space-y-4 w-2/3 mt-4" action="#" method="POST">
                        <Input type="text" label="Name"/>
                        <Textarea type="text" label="Description"/>
                        <Button type="submit" className="w-full">Create</Button>
                    </form>
                </Tab>
            </Tabs>

            <Modal isOpen={isOpen} onOpenChange={onOpenChange}>
                <ModalContent>
                    {(onClose) => (
                        <>
                            <ModalHeader className="flex flex-col gap-1">Edit chat</ModalHeader>
                            <ModalBody>
                                <form className="space-y-4" action="#" method="POST">
                                    <Input type="text" label="Name" defaultValue={currentChat?.name}/>
                                    <Textarea type="text" label="Description" defaultValue={currentChat?.description}/>
                                </form>
                            </ModalBody>
                            <ModalFooter className="flex flex-row justify-between">
                                <Button color="danger" onPress={onClose}>Delete chat</Button>
                                <Button color="primary" onPress={onClose}>Save changes</Button>
                            </ModalFooter>
                        </>
                    )}
                </ModalContent>
            </Modal>
        </div>
    );
}
