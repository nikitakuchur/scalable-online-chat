import {Modal, ModalBody, ModalContent, ModalFooter, ModalHeader} from "@nextui-org/modal";
import {Input, Textarea} from "@nextui-org/input";
import {Button} from "@nextui-org/button";
import {FormEvent, useState} from "react";
import {jwtFetch} from "@/utils/jwt-fetch";

export interface UpdateChatModalProps {
    currentChat: Chat | null;
    isOpen: boolean;
    onOpenChange: any;
    onUpdate: () => void;
    onDelete: () => void;
}

interface Errors {
    name?: string;
    description?: string;
}

export default function UpdateChatModal(props: UpdateChatModalProps) {
    const [errors , setErrors] = useState<Errors>({});

    function isInvalid(field?: string): boolean {
        return field != null;
    }

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()

        const formData = new FormData(event.currentTarget)
        const name = formData.get('name');
        const description = formData.get('description');

        if (props.currentChat == null) {
            return;
        }

        const chatId = props.currentChat.id;

        const response = await jwtFetch('/api/chats/' + chatId, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, description }),
        })

        if (response.ok) {
            props.onUpdate();
            return;
        }
        if (response.status === 400) {
            const errors = await response.json();
            setErrors(errors);
        } else {
            console.error(await response.text());
        }
    }

    async function onDelete() {
        if (props.currentChat == null) {
            return;
        }

        const chatId = props.currentChat.id;

        const response = await jwtFetch('/api/chats/' + chatId, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
        })

        if (response.ok) {
            props.onDelete();
            return;
        }
        console.error(await response.text());
    }

    return (<Modal isOpen={props.isOpen} onOpenChange={props.onOpenChange}>
        <ModalContent>
            <form className="space-y-4" onSubmit={handleSubmit} onChange={() => setErrors({})}>
                <ModalHeader className="flex flex-col gap-1">Edit chat</ModalHeader>
                <ModalBody>
                    <Input type="text" label="Name" name="name" defaultValue={props.currentChat?.name}
                           isInvalid={isInvalid(errors.name)} errorMessage={errors.name}/>
                    <Textarea type="text" label="Description" name="description" defaultValue={props.currentChat?.description}
                              isInvalid={isInvalid(errors.description)} errorMessage={errors.description}/>
                </ModalBody>
                <ModalFooter className="flex flex-row justify-between">
                    <Button color="danger" onPress={onDelete}>Delete chat</Button>
                    <Button color="primary" type="submit">Save changes</Button>
                </ModalFooter>
            </form>
        </ModalContent>
    </Modal>);
}
