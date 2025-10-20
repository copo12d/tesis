import { useCallback, useEffect, useState } from "react";
import { NotificationsAPI } from "../api/api.notifications";
import { FaLastfmSquare } from "react-icons/fa";
import toast from "react-hot-toast";



export function useNotifications({
    autoFetch = false
}){
    const [items,setItems] = useState([])
    const [error,setError] = useState([])
    const [loading,setLoading] = useState(false)

    const fetchContainersFull = useCallback(async () => {
        setLoading(true)
        setError(null)
        try {
            const res = await NotificationsAPI.inform()
            const content = res?.data?.data.content
            setItems(content)
            toast.success()

        } catch (err) {
            const msg = 
            err.response?.data?.errorsP?.[0]?.message ||
            err.response?.data?.meta?.message ||
            err.response?.data?.error ||
            'Error al registrar';
            setError(msg)
            toast.error(error)
            return { success: false, error: msg };
        }
    })

    useEffect(() => {
        if (autoFetch) fetchContainersFull()
    },[autoFetch,fetchContainersFull])
    
    return{
        items,
    }
}