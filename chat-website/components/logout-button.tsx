"use client";

import {Button} from "@nextui-org/button";
import {useRouter} from "next/navigation";
import {jwtFetch} from "@/utils/jwt-fetch";
import {useContext} from "react";
import {AuthContext} from "@/app/providers";

export default function LogoutButton() {
    const router = useRouter()
    const { user, setUser } = useContext(AuthContext);

    async function handleClick() {
        const response = await jwtFetch('/api/logout', { method: 'POST' });

        if (response.ok) {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            setUser(null);
            router.push("/login");
            return;
        }
    }

    if (user != null) {
        return <Button onClick={handleClick} variant="bordered" size="sm">Log out</Button>;
    }
    return <></>
}
