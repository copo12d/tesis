import { Avatar, Menu, Portal, Float, Circle } from "@chakra-ui/react";
import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import AuthContext from "../context/AuthContext";

export function UserProfileBubble() {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  return (
    <Menu.Root positioning={{ placement: "right-end" }}>
      <Menu.Trigger rounded="full" focusRing="outside">
        <Avatar.Root size="sm" colorPalette={"green"} color="white">
          <Avatar.Fallback name={user?.sub || user?.userName || "Usuario"} />
          <Float placement="bottom-end" offsetX="1" offsetY="1">
            <Circle
              bg="green.500"
              size="8px"
              outline="0.2em solid"
              outlineColor="bg"
            />
          </Float>
        </Avatar.Root>
      </Menu.Trigger>
      <Portal>
        <Menu.Positioner>
          <Menu.Content
            boxShadow={"sm"}
            p={2}
            bg="white"
            borderColor="teal.100"
          >
            <Menu.Item
              value="profile"
              onClick={() => navigate("/profile")}
              _hover={{ bg: "teal.50", color: "teal.700" }}
              _focus={{ bg: "teal.100", color: "teal.800" }}
              color="teal.700"
              rounded="md"
              px={3}
            >
              Perfil
            </Menu.Item>
            <Menu.Item
              value="logout"
              _hover={{ bg: "red.50", color: "red.600" }}
              _focus={{ bg: "red.100", color: "red.700" }}
              color="red.600"
              rounded="md"
              px={3}
            >
              Cerrar sesión
            </Menu.Item>
          </Menu.Content>
        </Menu.Positioner>
      </Portal>
    </Menu.Root>
  );
}
