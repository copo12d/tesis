import { Field, NativeSelect } from "@chakra-ui/react";

export function StyledSelectField({  label,  name,  value,  onChange,  options = [],  required = false,  error,  disabled = false,  placeholder = "Seleccione una opción",}) {
  return (
    <Field.Root required={required} invalid={!!error}>
      <Field.Label htmlFor={name} color="black">
        {label}
      </Field.Label>

      <NativeSelect.Root size="lg">
        <NativeSelect.Field
          id={name}
          name={name}
          value={value ?? ""}
          onChange={onChange}
          disabled={disabled}
          color="blackAlpha.900"
          pl={2}
        >
          <option
            value=""
            style={{
              backgroundColor: "white",
              color: "#a0a0a0",
              fontStyle: "italic",
            }}
          >
            {placeholder}
          </option>
          {options.map((opt) => (
            <option
              key={opt.value}
              value={opt.value}
              style={{ backgroundColor: "white", color: "black" }}
            >
              {opt.label}
            </option>
          ))}
        </NativeSelect.Field>
        <NativeSelect.Indicator />
      </NativeSelect.Root>

      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
}
