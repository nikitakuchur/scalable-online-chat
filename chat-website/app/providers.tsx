"use client";

import * as React from "react";
import { NextUIProvider } from "@nextui-org/system";
import {usePathname, useRouter} from 'next/navigation'
import { ThemeProvider as NextThemesProvider } from "next-themes";
import { ThemeProviderProps } from "next-themes/dist/types";
import {createContext, useEffect, useState} from "react";
import {jwtDecode} from "jwt-decode";

export interface ProvidersProps {
	children: React.ReactNode;
	themeProps?: ThemeProviderProps;
}

export interface User {
	sub: string,
	role: string,
	sessionId: string,
}

export interface AuthState {
	user: User | null,
	setUser: (user: User | null) => void,
}

export const AuthContext = createContext<AuthState>({ user: null, setUser: () => {} });

export function Providers({ children, themeProps }: ProvidersProps) {
	const router = useRouter();
	const pathname = usePathname();

	const [user, setUser] = useState<User | null>(null);
	const authContextValue: AuthState = { user, setUser };

	useEffect(() => {
		const accessToken = localStorage.getItem("accessToken");
		if (accessToken != null) {
			setUser(jwtDecode(accessToken));
		} else if (pathname != "/login" && pathname != "/signup") {
			router.push("/login");
		}
	}, [pathname, router]);

	return (
	  <NextUIProvider navigate={router.push}>
		  <NextThemesProvider {...themeProps}>
			  <AuthContext.Provider value={authContextValue}>
				  {children}
			  </AuthContext.Provider>
		  </NextThemesProvider>
	  </NextUIProvider>
  );
}
