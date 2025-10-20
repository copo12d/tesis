import api from "@/api/api";

export const NotificationsAPI = {
    inform: api.get("/container/admin/alerts/full")
}