import {
	Navbar as NextUINavbar,
	NavbarContent,
	NavbarBrand,
} from "@nextui-org/navbar";

import NextLink from "next/link";

import { ThemeSwitch } from "@/components/theme-switch";
import LogoutButton from "@/components/logout-button";

export const Navbar = () => {
	return (
		<NextUINavbar maxWidth="xl" position="sticky">
			<NavbarContent className="basis-1/5 sm:basis-full" justify="start">
				<NavbarBrand as="li" className="gap-3 max-w-fit">
					<NextLink className="flex justify-start items-center gap-1" href="/">
						<p className="font-bold text-inherit">Online Chat</p>
					</NextLink>
				</NavbarBrand>
			</NavbarContent>
			<NavbarContent className="basis-1 pl-4" justify="end">
				<ThemeSwitch />
				<LogoutButton />
			</NavbarContent>
		</NextUINavbar>
	);
};
