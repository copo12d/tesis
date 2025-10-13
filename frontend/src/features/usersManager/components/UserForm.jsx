import {
  Stack,
  Button,
  Text,
  Box,
} from "@chakra-ui/react";
import {
  LiaUser,
  LiaLockSolid,
  LiaIdBadgeSolid,
  LiaAtSolid,
} from "react-icons/lia";
import { useState, useEffect, useContext } from "react";
import { toast } from "react-hot-toast";
import AuthContext from "@/context/AuthContext";
import { availableRolesFor } from "../api/user.api";
import { IconInputField } from "@/components/ui/IconInputField";
import { StyledSelectField } from "@/components/ui/StyledSelectField";

// Hook integrado
function useUserForm({ initialValues = {}, includeRole = true, onSubmit }) {
  const [form, setForm] = useState({
    fullName: "",
    userName: "",
    email: "",
    password: "",
    repeatPassword: "",
    role: "",
    ...initialValues,
  });

  const [errors, setErrors] = useState({});

  const validate = (values) => {
    const newErrors = {};

    if (!values.fullName) {
      newErrors.fullName = "El nombre es obligatorio";
    } else if (/\d/.test(values.fullName)) {
      newErrors.fullName = "El nombre no debe contener números";
    }

    if (!values.userName) {
      newErrors.userName = "El usuario es obligatorio";
    }

    if (!values.email) {
      newErrors.email = "El correo es obligatorio";
    }

    if (!values.password && !values.id) {
      newErrors.password = "La contraseña es obligatoria";
    }

    if (!values.repeatPassword && !values.id) {
      newErrors.repeatPassword = "Debes repetir la contraseña";
    }

    if (
      values.password &&
      values.repeatPassword &&
      values.password !== values.repeatPassword
    ) {
      newErrors.repeatPassword = "Las contraseñas no coinciden";
    }

    if (includeRole && !values.role) {
      newErrors.role = "Debes seleccionar un rol";
    }

    setErrors(newErrors);
    return newErrors;
  };

  const setField = (field, value) => {
    const updated = { ...form, [field]: value };
    setForm(updated);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const validationErrors = validate(form);
    if (Object.keys(validationErrors).length === 0) {
      onSubmit(form);
    }
  };

  return {
    form,
    errors,
    setField,
    handleSubmit,
    includeRole,
    validate,
  };
}

// Componente principal
export function UserForm({
  loading = false,
  initialValues = {},
  includeRole = true,
  onSubmit,
  submitText = "Guardar",
  title,
  fields,
}) {
  const { user } = useContext(AuthContext);
  const [roles, setRoles] = useState([]);

  const {
    form,
    errors,
    setField,
    handleSubmit,
    includeRole: includeRoleComputed,
    validate,
  } = useUserForm({ initialValues, includeRole, onSubmit });

  const isEdit = !!initialValues?.id;

  useEffect(() => {
    async function fetchRoles() {
      const result = await availableRolesFor(user?.role);
      console.log(result);
      setRoles(result);
    }
    fetchRoles();
  }, [user?.role]);

  const FIELDS = [
    {
      name: "fullName",
      label: "Nombre completo",
      placeholder: "Nombre completo",
      icon: <LiaIdBadgeSolid />,
      type: "text",
      autoComplete: "name",
    },
    {
      name: "userName",
      label: "Usuario",
      placeholder: "Usuario",
      icon: <LiaUser />,
      type: "text",
      autoComplete: "username",
    },
    {
      name: "email",
      label: "Email",
      placeholder: "correo@ejemplo.com",
      icon: <LiaAtSolid />,
      type: "email",
      autoComplete: "email",
    },
    {
      name: "password",
      label: "Contraseña",
      placeholder: isEdit ? "(dejar en blanco para no cambiar)" : "********",
      icon: <LiaLockSolid />,
      type: "password",
      autoComplete: isEdit ? "off" : "new-password",
    },
    {
      name: "repeatPassword",
      label: "Repetir contraseña",
      placeholder: "********",
      icon: <LiaLockSolid />,
      type: "password",
      autoComplete: "off",
    },
  ];

  const filteredFields = fields
    ? FIELDS.filter((f) => fields.includes(f.name))
    : FIELDS;

  const handleFieldChange = (name, value) => {
    setField(name, value);
  };

  const handleValidatedSubmit = (e) => {
    e.preventDefault();
    const validationErrors = validate(form);
    if (Object.keys(validationErrors).length > 0) {
      toast.error("Debes llenar todos los campos");
      return;
    }
    onSubmit(form);
  };

  return (
    <Stack
      spacing={0}
      borderRadius="md"
      boxShadow="md"
      borderWidth={1}
      borderColor="green.600"
      bg="whiteAlpha.900"
      maxW="6xl"
      mx="auto"
      mt={6}
    >
      <Box
        bg="green.600"
        color="white"
        px={6}
        py={4}
        borderTopRadius="md"
        borderBottom="1px solid"
        borderColor="green.700"
      >
        <Text fontSize="xl" fontWeight="bold">
          {title || (isEdit ? "Editar usuario" : "Nuevo usuario")}
        </Text>
      </Box>

      <Box px={6} py={6}>
        <form onSubmit={handleValidatedSubmit}>
          <Stack spacing={6}>
            {filteredFields.map((f) => (
              <IconInputField
                key={f.name}
                label={f.label}
                name={f.name}
                value={form[f.name] ?? ""}
                onChange={(e) => handleFieldChange(f.name, e.target.value)}
                placeholder={f.placeholder}
                icon={f.icon}
                iconProps={{ bg: "teal.700", px: 3 }}
                type={f.type}
                disabled={loading}
                error={errors[f.name]}
                inputProps={{
                  autoComplete: f.autoComplete,
                  w: "100%",
                  pl: 2,
                  _placeholder: { pl: 2 },
                }}
              />
            ))}

            {includeRoleComputed && (
              <StyledSelectField
                label="Rol"
                name="role"
                value={form.role ?? ""}
                onChange={(e) => handleFieldChange("role", e.target.value)}
                options={roles.map((r) => ({
                  value: r.label,
                  label: r.value,
                }))}
                error={errors.role}
                disabled={loading}
                placeholder="Seleccione una opción"
              />
            )}

            <Button
              type="submit"
              colorPalette="green"
              size="lg"
              loading={loading}
              loadingText="Guardando..."
              spinnerPlacement="end"
              alignSelf="flex-end"
              px={2}
            >
              {submitText}
            </Button>
          </Stack>
        </form>
      </Box>
    </Stack>
  );
}
