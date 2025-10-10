import { Menu, Button, Portal } from "@chakra-ui/react";

export default function MenuSelect({ label, value, options, onChange, icon }) {
  return (
    <Menu.Root>
      <Menu.Trigger asChild>
        <Button variant="outline" size="sm" w="100%" mb={2} leftIcon={icon}>
          {label}: {options.find((o) => o.value === value)?.label || "Seleccionar"}
        </Button>
      </Menu.Trigger>
      <Portal>
        <Menu.Positioner>
          <Menu.Content minW="12rem" zIndex={1700}>
            <Menu.RadioItemGroup
              value={value}
              onValueChange={(e) => onChange(e.value)}
            >
              {options.map((item) => (
                <Menu.RadioItem key={item.value} value={item.value}>
                  {item.label}
                  <Menu.ItemIndicator />
                </Menu.RadioItem>
              ))}
            </Menu.RadioItemGroup>
          </Menu.Content>
        </Menu.Positioner>
      </Portal>
    </Menu.Root>
  );
}