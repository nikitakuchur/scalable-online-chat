import {Card, CardBody, CardHeader} from "@nextui-org/card";
import {useContext, useState} from "react";
import {AuthContext} from "@/app/providers";
import moment, {now} from "moment/moment";
import {Moment} from "moment";

export interface Message {
    id: string,
    sender: string,
    text: string,
    timestamp: string,
    type: string,
}

export interface MessageListProps {
    messages: Message[],
    loadMore: (lastTimestamp: Moment) => void;
}

export default function MessageList(props: MessageListProps) {
    const { user} = useContext(AuthContext);
    const [ messageSet, setMessageSet ] = useState<Set<Message>>(new Set(props.messages));

    function calculateTimestamp(message: Message) {
        const timestamp = moment(message.timestamp!);
        if (timestamp.isSame(now(), 'day')) {
            return timestamp.format("hh:mm");
        }
        return timestamp.format("DD.MM.YYYY hh:mm");
    }

    function renderUserMessage(message: Message) {
        return (
            <Card key={message.id} className={"max-w-96 shrink-0 " + (message.sender !== user?.sub ? "self-start" : "self-end")} shadow="sm">
                { message.sender !== user?.sub && (<CardHeader className="text-xs pb-0">{message.sender}</CardHeader>)}
                <CardBody className="flex flex-col gap-x-4 items-end">
                    <p className="place-self-start">{message.text}</p>
                    <div className="text-xs pt-0 text-gray-400">{calculateTimestamp(message)}</div>
                </CardBody>
            </Card>
        );
    }

    function renderServiceMessage(message: Message) {
        return (
            <div key={message.id} className="place-self-center text-gray-500 text-sm">{message.text}</div>
        );
    }

    // this function is used to remove any duplicate messages if they appear due to at-least-once delivery
    function onlyUnique(message: Message, index: number, array: Message[]) {
        return array.findIndex((item) => item.id == message.id ) === index;
    }

    return (
        <div className="p-4 flex flex-col-reverse gap-y-2 flex-nowrap flex-grow overflow-scroll h-96"
             onScroll={(event) => {
                 const scrollHeight = event.currentTarget.scrollHeight;
                 const clientHeight = event.currentTarget.clientHeight;
                 const scrollTop = event.currentTarget.scrollTop;

                 if (scrollHeight - clientHeight + scrollTop == 0) {
                     const lastTimestamp = props.messages[props.messages.length - 1].timestamp;
                     console.log("Loading more messages...");
                     props.loadMore(moment(lastTimestamp));
                 }
             }}>
            {props.messages.filter(onlyUnique).map(message =>
                message.type == "USER_MESSAGE" ? renderUserMessage(message) : renderServiceMessage(message)
            )}
        </div>
    );
}
