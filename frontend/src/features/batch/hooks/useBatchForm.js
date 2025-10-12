import { useState, useCallback } from "react";

const INITIAL = {
  description: "",
};

export function useBatchForm({ initialValues = {}, onSubmit } = {}) {
  const [form, setForm] = useState({ ...INITIAL, ...initialValues });
  const [errors, setErrors] = useState({});

  const setField = useCallback(
    (k, v) => {
      setForm(f => ({ ...f, [k]: v }));
    },
    []
  );

  const validate = useCallback(() => {
    const e = {};
    if (!form.description.trim() || form.description.trim().length < 3) {
      e.description = "La descripción es obligatoria";
    }
    setErrors(e);
    return Object.keys(e).length === 0;
  }, [form]);

  const handleSubmit = useCallback(
    (ev) => {
      ev.preventDefault();
      if (!validate()) return;
      const payload = {
        description: form.description,
      };
      onSubmit && onSubmit(payload);
    },
    [form, onSubmit, validate]
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
  };
}