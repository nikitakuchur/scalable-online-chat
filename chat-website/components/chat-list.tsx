"use client";

import {Tab, Tabs} from "@nextui-org/tabs";
import {Card, CardBody, CardFooter, CardHeader} from "@nextui-org/card";
import NextLink from "next/link";
import {Pagination} from "@nextui-org/pagination";
import {useDisclosure} from "@nextui-org/modal";
import {useContext, useEffect, useState} from "react";
import {jwtFetch} from "@/utils/jwt-fetch";
import {AuthContext} from "@/app/providers";
import CreateChatForm from "@/components/create-chat-form";
import UpdateChatModal from "@/components/update-chat-modal";
import SearchBar from "@/components/search-bar";

interface Page {
    content: Chat[],
    number: number,
    totalPages: number,
}

export default function ChatList() {
    const [allChatsSearchPhrase, setAllChatsSearchPhrase] = useState<string>("");
    const [allChatsPage, setAllChatsPage] = useState<Page>({ content: [], number: 0, totalPages: 1 });

    const [myChatsSearchPhrase, setMyChatsSearchPhrase] = useState<string>("");
    const [myChatsPage, setMyChatsPage] = useState<Page>({ content: [], number: 0, totalPages: 1 });

    const {isOpen, onClose, onOpen, onOpenChange} = useDisclosure();
    const [currentChat, setCurrentChat] = useState<Chat | null>(null);

    const { user } = useContext(AuthContext);

    async function loadChatPage(page: number, searchPhrase?: string, username?: string) {
        const params = new URLSearchParams({
            "size": "5",
            "page": (page - 1).toString()
        });
        if (searchPhrase) {
            params.append("searchPhrase", searchPhrase);
        }
        if (username) {
            params.append("owner", username);
        }
        const response = await jwtFetch('/api/chats?' + params, { method: 'GET' });

        if (response.ok) {
            return await response.json();
        }
        return { content: [], number: 0, totalPages: 1, };
    }

    function loadAllChatsPage(page: number) {
        loadChatPage(page, allChatsSearchPhrase).then(page => setAllChatsPage(page));
    }

    function loadMyChatsPage(page: number) {
        loadChatPage(page, myChatsSearchPhrase, user?.sub).then(page => setMyChatsPage(page));
    }

    function softDataReload() {
        if (user != null) {
            loadAllChatsPage(allChatsPage.number + 1);
            loadMyChatsPage(myChatsPage.number + 1);
        }
    }

    function hardDataReload() {
        if (user != null) {
            loadAllChatsPage(1);
            loadMyChatsPage(1);
        }
    }

    useEffect(() => {
        hardDataReload();
    },[user, allChatsSearchPhrase, myChatsSearchPhrase]);

    const toChatCard = (chat : Chat) => (
        <Card key={chat.id} className="shrink-0 w-5/6 self-center m-2" shadow="sm">
            <CardHeader className="pb-0 flex flex-row justify-between">
                <p>{chat.name}</p>
                {(chat.owner === user?.sub || user?.role === "ADMIN") && (
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

    return (
        <div>
            <Tabs fullWidth aria-label="Options" placement="top">
                <Tab key="all-chats" title="All chats">
                    <div className="flex flex-nowrap flex-col mt-4 mb-4">
                        <SearchBar defaultValue={allChatsSearchPhrase} onSearch={setAllChatsSearchPhrase} />
                        {allChatsPage.content.map(toChatCard)}
                        <Pagination className="self-center mt-4"
                                    total={allChatsPage.totalPages != 0 ? allChatsPage.totalPages : 1}
                                    page={allChatsPage.number + 1}
                                    onChange={loadAllChatsPage} />
                    </div>
                </Tab>
                <Tab key="my-chats" title="My chats">
                    <div className="flex flex-nowrap flex-col mt-4 mb-4">
                        <SearchBar defaultValue={myChatsSearchPhrase} onSearch={setMyChatsSearchPhrase} />
                        {myChatsPage.content.map(toChatCard)}
                        <Pagination className="self-center mt-4"
                                    total={myChatsPage.totalPages != 0 ? myChatsPage.totalPages : 1}
                                    page={myChatsPage.number + 1}
                                    onChange={loadMyChatsPage} />
                    </div>
                </Tab>
                <Tab key="create-chat" title="Create new chat" className="flex justify-center">
                    <CreateChatForm />
                </Tab>
            </Tabs>

            <UpdateChatModal currentChat={currentChat}
                             isOpen={isOpen}
                             onOpenChange={onOpenChange}
                             onDelete={() => {
                                 hardDataReload();
                                 onClose();
                             }}
                             onUpdate={() => {
                                 softDataReload();
                                 onClose();
                             }} />
        </div>
    );
}
