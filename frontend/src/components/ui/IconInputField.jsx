import { Field, InputGroup, Input, Text } from "@chakra-ui/react";

export function IconInputField({
  label,
  name,
  value,
  onChange,
  placeholder,
  icon,
  iconProps = {},
  type = "text",
  required = false,
  disabled = false,
  error,
  size = "lg",
  inputProps = {},
}) {
  return (
    <Field.Root required={required} invalid={!!error}>
      <Field.Label htmlFor={name} color="black">
        {label}
      </Field.Label>
      <InputGroup startAddon={icon} startAddonProps={iconProps}>
        <Input
          id={name}
          name={name}
          type={type}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          size={size}
          color="blackAlpha.900"
          isDisabled={disabled}
          {...inputProps}
        />
      </InputGroup>
      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
}
