import { Menu, Button, Portal, Box, Text } from "@chakra-ui/react";

/**
 * MenuSelect
 *
 * Props:
 * - label: string
 * - value: any
 * - options: [{ value, label, ...extra }]
 * - onChange: function
 * - icon: ReactNode
 * - buttonProps: props extra para el botón
 * - menuProps: props extra para Menu.Content
 * - renderItem: función para renderizar cada opción personalizada (opcional)
 * - renderLabel: función para renderizar el label del botón (opcional)
 */
export default function MenuSelect({
  label,
  value,
  options,
  onChange,
  icon,
  buttonProps = {},
  menuProps = {},
  renderItem,
  renderLabel,
  ...rest
}) {
  const selected = options.find((o) => o.value === value);

  return (
    <Menu.Root {...rest}>
      <Menu.Trigger asChild>
        <Button
          variant="outline"
          size="sm"
          w="100%"
          mb={2}
          leftIcon={icon}
          fontWeight="medium"
          colorScheme="teal"
          borderRadius="md"
          boxShadow="sm"
          _hover={{ bg: "teal.50", borderColor: "teal.400" }}
          {...buttonProps}
        >
          {renderLabel
            ? renderLabel(selected, label)
            : (
              <Text as="span" color="teal.700">
                {label}: <b>{selected?.label || "Seleccionar"}</b>
              </Text>
            )}
        </Button>
      </Menu.Trigger>
      <Portal>
        <Menu.Positioner>
          <Menu.Content
            minW="14rem"
            zIndex={1700}
            borderRadius="md"
            boxShadow="lg"
            bg="white"
            borderWidth="1px"
            borderColor="teal.100"
            py={2}
            {...menuProps}
          >
            <Menu.RadioItemGroup
              value={value}
              onValueChange={(e) => onChange(e.value)}
            >
              {options.map((item) =>
                renderItem ? (
                  renderItem(item, value)
                ) : (
                  <Menu.RadioItem
                    key={item.value}
                    value={item.value}
                    px={4}
                    py={2}
                    borderRadius="md"
                    _hover={{
                      bg: "teal.50",
                      color: "teal.700",
                    }}
                    _checked={{
                      bg: "teal.100",
                      color: "teal.900",
                      fontWeight: "bold",
                    }}
                    color={"blackAlpha.900"}
                    transition="all 0.15s"
                  >
                    <Box display="flex" alignItems="center" w="100%">
                      <Text flex="1">{item.label}</Text>
                      <Menu.ItemIndicator ml={193} color="teal.600" />
                    </Box>
                  </Menu.RadioItem>
                )
              )}
            </Menu.RadioItemGroup>
          </Menu.Content>
        </Menu.Positioner>
      </Portal>
    </Menu.Root>
  );
}