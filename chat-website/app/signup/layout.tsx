import {Metadata} from "next";

export const metadata: Metadata = {
	title: 'Sign Up',
}

export default function PricingLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<section className="flex flex-col items-center justify-center gap-4 h-5/6">
			{children}
		</section>
	);
}
