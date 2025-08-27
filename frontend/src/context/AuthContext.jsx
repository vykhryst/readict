import {createContext, useContext, useEffect, useState} from "react";
import {getSession, login as apiLogin, logout as apiLogout} from "../api/auth";

const AuthContext = createContext(null);
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({children}) {
    const [user, setUser] = useState(null);      // null = loading
    const isGuest = !user || !user.email;

    const refresh = async () => {
        try {
            const {data} = await getSession();
            setUser(data);
        } catch {
            setUser({});
        }
    };
    useEffect(() => {
        refresh().then();
    }, []);

    /* ---- методи ---- */
    const login = async (email, password) => {
        await apiLogin(email, password);
        await refresh();
    };

    const logout = async () => {
        await apiLogout();
        setUser({});
    };

    const value = {user, login, logout, isGuest, loading: user === null};
    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
