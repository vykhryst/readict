import {createContext, useContext, useEffect, useState} from "react";
import {login as apiLogin, logout as apiLogout} from "../api/auth";
import {getMe as apiMe} from "../api/user.js";

const AuthContext = createContext(null);
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({children}) {
    const [user, setUser] = useState(null);      // null = loading
    const isGuest = !user || !user.email;

    /* ---- одноразове визначення сесії ---- */
    const refresh = async () => {
        try {
            const {data} = await apiMe();
            setUser(data);           // 200 OK
        } catch {
            setUser({});             // 204 або 401
        }
    };
    useEffect(() => {
        refresh().then(r => console.log("AuthContext: refresh()"));
    }, []);

    /* ---- методи ---- */
    const login = async (email, password) => {
        await apiLogin(email, password);
        await refresh();           // тепер контекст знає користувача
    };

    const logout = async () => {
        await apiLogout();
        setUser({});
    };

    const value = {user, login, logout, isGuest, loading: user === null};
    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
