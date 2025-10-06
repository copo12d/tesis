import { Flex, Input, InputGroup, Menu, Button, Portal, Box } from "@chakra-ui/react";
import { FaSearch } from "react-icons/fa";

export function SearchBar() {
  return (
    <Flex align="center" w="100%" maxW="400px">
      <Menu>
        <Menu.Button as={Button} variant="outline" size="sm" borderRightRadius={0}>
          Filtro
        </Menu.Button>
        <Portal>
          <Menu.List>
            <Menu.Item value="all">Todos</Menu.Item>
            <Menu.Item value="name">Nombre</Menu.Item>
            <Menu.Item value="email">Email</Menu.Item>
          </Menu.List>
        </Portal>
      </Menu>
      <InputGroup flex="1"
      startAddon={<FaSearch />}

      >
        <Input
          borderLeftRadius={0}
          borderColor="gray.300"
          placeholder="Buscar..."
          bg="white"
        />
      </InputGroup>
    </Flex>
  );
}