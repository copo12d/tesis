import { useContext, useEffect, useState, useCallback } from "react";
import AuthContext from "@/context/AuthContext";
import { UsersAPI } from "../api/user.api";

/**
 * useUserProfileFromToken
 * - toma username desde AuthContext.user.sub
 * - llama GET /users/public/idByUsername?username=...
 * - luego GET /users/public/{id} para obtener el perfil completo
 */
export function useUserProfileFromToken() {
  const { user } = useContext(AuthContext) || {};
  const username = user?.sub;
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const normalizeId = (resp) => {
    // resp puede venir en muchas formas: { data: { data: id } }, { data: id }, id
    if (resp == null) return null;
    if (typeof resp === "number" || typeof resp === "string") return resp;
    // axios response.data was assigned to resp in caller, try usual shapes:
    const maybe = resp?.data ?? resp;
    // maybe could be object { data: id } or { id: x } or direct id
    if (maybe == null) return null;
    if (typeof maybe === "number" || typeof maybe === "string") return maybe;
    if (maybe?.data != null) return maybe.data;
    if (maybe?.id != null) return maybe.id;
    return null;
  };

  const normalizeProfile = (resp) => {
    if (resp == null) return null;
    // resp is axios response.data or similar
    return resp?.data ?? resp;
  };

  const fetchProfile = useCallback(async () => {
    // start only when we have username
    if (!username) {
      setProfile(null);
      setError(null);
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const idResp = await UsersAPI.getIdByUsername(username);
      // idResp may be axios response; normalize
      console.log("ID response for username", username, ":", idResp);
      const idCandidate = normalizeId(idResp?.data ?? idResp);
      if (!idCandidate) throw new Error("No se obtuvo ID de usuario desde el servidor.");

      const profileResp = await UsersAPI.getProfile(idCandidate);
      const userData = normalizeProfile(profileResp?.data ?? profileResp);
      if (!userData) throw new Error("No se obtuvo perfil de usuario.");
      // keep only the fields we care about (fullName, userName) optionally
      setProfile({
        ...userData,
        fullName: userData.fullName ?? userData.full_name ?? "",
        userName: userData.userName ?? userData.user_name ?? userData.username ?? "",
      });
    } catch (err) {
      setError(err);
      setProfile(null);
    } finally {
      setLoading(false);
    }
  }, [username]);

  useEffect(() => {
    // trigger on mount and whenever username changes
    fetchProfile();
  }, [fetchProfile]);

  return {
    profile,
    loading,
    error,
    refresh: fetchProfile,
    setProfile,
  };
}