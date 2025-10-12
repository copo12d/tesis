import { Stack, Button, Text } from "@chakra-ui/react";
import { LiaFileAltSolid } from "react-icons/lia";
import { useState } from "react";
import { IconInputField } from "@/components/ui/IconInputField";

export function BatchForm({
  loading = false,
  initialValues = {},
  onSubmit,
  submitText = "Registrar lote",
  title = "Registrar nuevo lote",
}) {
  const [form, setForm] = useState({
    description: initialValues.description || "",
  });
  const [errors, setErrors] = useState({});

  const busy = loading;

  const setField = (name, value) => {
    setForm((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    let newErrors = {};
    if (!form.description || form.description.trim().length < 3) {
      newErrors.description = "La descripción es obligatoria";
    }
    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) return;
    onSubmit(form);
  };

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

        <IconInputField
          label="Descripción"
          name="description"
          value={form.description}
          onChange={(e) => setField("description", e.target.value)}
          placeholder="Descripción del lote"
          icon={<LiaFileAltSolid />}
          iconProps={{ bg: "teal.700", px: 3 }}
          required
          disabled={busy}
          error={errors.description}
          inputProps={{
            w: "100%",
            pl: 2,
            _placeholder: { pl: 2 },
            autoComplete: "off",
          }}
        />

        <Button
          type="submit"
          colorPalette="green"
          size="lg"
          isLoading={busy}
          loadingText="Registrando..."
          spinnerPlacement="end"
          alignSelf="flex-end"
          disabled={busy}
          px={2}
        >
          {submitText}
        </Button>

      </Stack>
    </form>
  );
}
