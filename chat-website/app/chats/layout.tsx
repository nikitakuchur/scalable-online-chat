export default function AboutLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<section className="flex flex-col h-full md:w-1/2 w-full">
			{children}
		</section>
	);
}
