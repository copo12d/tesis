import {
  Stack,
  Button,
  Input,
  InputGroup,
  Field,
  Text,
  NativeSelect,
} from "@chakra-ui/react";
import {
  LiaUser,
  LiaLockSolid,
  LiaIdBadgeSolid,
  LiaAtSolid,
} from "react-icons/lia";
import { useUserForm } from "../hooks/useUserForm";
import { availableRolesFor } from "../api/user.api";
import { useContext } from "react";
import AuthContext from "@/context/AuthContext";

export function UserForm({
  loading = false,
  initialValues = {},
  includeRole = true,
  onSubmit, // <-- ahora se usa el onSubmit que viene del padre
  submitText = "Guardar",
  title,
}) {
  const { user } = useContext(AuthContext);

  const roles = availableRolesFor(user?.role);

  const {
    form,
    errors,
    setField,
    handleSubmit,
    includeRole: includeRoleComputed,
  } = useUserForm({ initialValues, includeRole, onSubmit });

  const busy = loading;

  const iconAddonProps = { bg: "teal.700", px: 3 };

  const isEdit = !!initialValues?.id;

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
      required: !isEdit, // Solo requerido si es creación
    },
  ];

  return (
    <form onSubmit={handleSubmit}>
      <Stack
        spacing={6}
        p={4}
        bg="whiteAlpha.900"
        boxShadow="md"
        w="100%"
        h={"100vh"}
      >
        {title && (
          <Text fontSize="2xl" fontWeight="bold" mb={2} color="black">
            {title}
          </Text>
        )}

        {FIELDS.map((f) => (
          <Field.Root key={f.name} required={f.required} invalid={!!errors[f.name]}>
            <Field.Label color="black">{f.label}</Field.Label>
            <InputGroup
              startAddon={f.icon}
              startAddonProps={iconAddonProps}
            >
              <Input
                type={f.type}
                placeholder={f.placeholder}
                value={form[f.name]}
                onChange={(e) => setField(f.name, e.target.value)}
                size="lg"
                color="blackAlpha.900"
                autoComplete={f.autoComplete}
                isDisabled={busy}
              />
            </InputGroup>
            {errors[f.name] && (
              <Field.ErrorText>{errors[f.name]}</Field.ErrorText>
            )}
          </Field.Root>
        ))}

        {includeRoleComputed && (
          <Field.Root required invalid={!!errors.role}>
            <Field.Label color="black">Rol</Field.Label>
            <NativeSelect.Root size="lg">
              <NativeSelect.Field
                value={form.role}
                onChange={(e) => setField("role", e.target.value)}
                color="blackAlpha.900"
                disabled={busy}
              >
                {roles.map((r) => (
                  <option
                    key={r.value}
                    value={r.value}
                    style={{ backgroundColor: "white", color: "black" }}
                  >
                    {r.label}
                  </option>
                ))}
              </NativeSelect.Field>
              <NativeSelect.Indicator />
            </NativeSelect.Root>
            {errors.role && (
              <Field.ErrorText>{errors.role}</Field.ErrorText>
            )}
          </Field.Root>
        )}

        <Button
          type="submit"
          colorPalette="green"
          size="lg"
          loading={busy}
          loadingText="Guardando..."
          spinnerPlacement="end"
          alignSelf="flex-end"
          disabled={busy}
          px={2}
        >
          {submitText}
        </Button>

        {Object.keys(errors).length > 0 && (
          <Text fontSize="sm" color="red.500">
            Corrige los campos marcados.
          </Text>
        )}
      </Stack>
    </form>
  );
}
