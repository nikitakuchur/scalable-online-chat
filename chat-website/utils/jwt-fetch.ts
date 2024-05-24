export const jwtFetch: typeof fetch = async (url, params) => {
    const accessToken = localStorage.getItem("accessToken");
    (params?.headers as any)["Authorization"] = `Bearer ${accessToken}`;
    let data = await fetch(url, params);

    let count = 0;
    while (data.status == 403 && count < 5) {
        await retrieveTokens();
        const accessToken = localStorage.getItem("accessToken");
        (params?.headers as any)["Authorization"] = `Bearer ${accessToken}`;
        data = await fetch(url, params);
        if (data.status != 403) {
            break;
        }
        await sleep(1000);
        count++;
    }

    return data;
}

const sleep = (ms: number) => new Promise(r => setTimeout(r, ms));

/**
 * Retrieves access and refresh token and puts them into the local storage.
 */
export async function retrieveTokens() {
    console.log("Retrieving tokens...")
    const tokenResponse = await fetch('/api/refresh-token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ refreshToken: localStorage.getItem("refreshToken") })
    });
    if (!tokenResponse.ok) {
        const error = "An error occurred while retrieving new tokens.";
        console.error(error, await tokenResponse.text());
        return Promise.reject(error);
    }
    const tokens = await tokenResponse.json();
    localStorage.setItem("accessToken", tokens.accessToken);
    localStorage.setItem("refreshToken", tokens.refreshToken);
}
