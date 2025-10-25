import {
  Field,
  Select,
  Box,
  Icon,
  Text,
} from "@chakra-ui/react";

export default function MenuSelect({
  label,
  value,
  options,
  onChange,
  icon,
  error,
  required = false,
  placeholder = "Seleccione una opción",
  disabled = false,
}) {
  return (
    <Field.Root isRequired={required} isInvalid={!!error}>
      <Field.Label fontWeight="medium" color="teal.700">
        {label}
        {required && <Field.RequiredIndicator />}
      </Field.Label>

      <Field.Control asChild>
        <Box position="relative">
          {icon && (
            <Icon
              as={icon}
              position="absolute"
              left={2}
              top="50%"
              transform="translateY(-50%)"
              color="teal.600"
              boxSize={5}
              pointerEvents="none"
            />
          )}
          <Select
            value={value}
            onChange={(e) => onChange(e.target.value)}
            pl={icon ? 8 : 3}
            borderColor="teal.300"
            _hover={{ borderColor: "teal.500" }}
            _focus={{ borderColor: "teal.600", boxShadow: "none" }}
            placeholder={placeholder}
            disabled={disabled}
          >
            {options.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </Select>
        </Box>
      </Field.Control>

      {error && <Field.ErrorText>{error}</Field.ErrorText>}
      <Field.HelperText>
        {required ? "Este campo es obligatorio" : "Opcional"}
      </Field.HelperText>
    </Field.Root>
  );
}
