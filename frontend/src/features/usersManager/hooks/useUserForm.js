import { useEffect, useState, useCallback } from "react";

const INITIAL = {
  fullName: "",
  userName: "",
  email: "",
  password: "",
  role: "ROLE_USER",
};

export function useUserForm({ initialValues = {}, includeRole = true, onSubmit } = {}) {
  const [form, setForm] = useState({ ...INITIAL, ...initialValues });
  const [errors, setErrors] = useState({});



  const setField = useCallback(
    (k, v) => {
      setForm(f => ({ ...f, [k]: v }));
    },
    []
  );

  const isEdit = !!initialValues.id;

  const validate = useCallback(() => {
    const e = {};
    if (!form.fullName.trim()) e.fullName = "Requerido";
    if (!form.userName.trim()) e.userName = "Requerido";
    if (!form.email.trim()) e.email = "Requerido";
    // Solo pide mínimo 8 caracteres si es creación o si el usuario escribió algo en edición
    if ((!isEdit && form.password.length < 8) || (isEdit && form.password && form.password.length < 8)) {
      e.password = "Mínimo 8 caracteres";
    }
    if (includeRole && !form.role) e.role = "Requerido";
    setErrors(e);
    return Object.keys(e).length === 0;
  }, [form, includeRole, isEdit]);

  const handleSubmit = useCallback(
    (ev) => {
      ev.preventDefault();
      if (!validate()) return;
      const payload = {
        fullName: form.fullName,
        userName: form.userName,
        email: form.email,
        ...(form.password ? { password: form.password } : {}),
        ...(includeRole ? { role: form.role } : {}),
      };
      onSubmit && onSubmit(payload);
    },
    [form, includeRole, onSubmit, validate]
  );

  const reset = useCallback(() => {
    setForm({ ...INITIAL, ...initialValues });
    setErrors({});
  }, [initialValues]);

  return {
    form,
    errors,
    setField,
    handleSubmit,
    reset,
    includeRole,
  };
}