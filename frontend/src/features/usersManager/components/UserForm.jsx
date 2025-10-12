import {
  Stack,
  Button,
  Text,
  NativeSelect,
  Field,
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
import { IconInputField } from "@/components/ui/IconInputField";

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
  const roles = availableRolesFor(user?.role);

  const {
    form,
    errors,
    setField,
    handleSubmit,
    includeRole: includeRoleComputed,
  } = useUserForm({ initialValues, includeRole, onSubmit });

  const busy = loading;
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
      required: !isEdit,
    },
  ];

  const filteredFields = fields
    ? FIELDS.filter((f) => fields.includes(f.name))
    : FIELDS;

  return (
    <form onSubmit={handleSubmit}>
      <Stack
        spacing={6}
        p={4}
        bg="whiteAlpha.900"
        boxShadow="md"
        w="100%"
        h="100vh"
      >
        {title && (
          <Text fontSize="2xl" fontWeight="bold" mb={2} color="black">
            {title}
          </Text>
        )}

        {filteredFields.map((f) => (
          <IconInputField
            key={f.name}
            label={f.label}
            name={f.name}
            value={form[f.name]}
            onChange={(e) => setField(f.name, e.target.value)}
            placeholder={f.placeholder}
            icon={f.icon}
            iconProps={{ bg: "teal.700", px: 3 }}
            type={f.type}
            required={f.required}
            disabled={busy}
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
          isLoading={busy}
          loadingText="Guardando..."
          spinnerPlacement="end"
          alignSelf="flex-end"
          disabled={busy}
          px={2}
        >
          {submitText}
        </Button>

        {Object.values(errors).some((msg) => !!msg) && (
          <Text fontSize="sm" color="red.500">
            Corrige los campos marcados.
          </Text>
        )}
      </Stack>
    </form>
  );
}
