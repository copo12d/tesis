import React from "react";
import {
  Stack,
  Table,
  For,
  Text,
  Box,
  Flex,
  Card,
  ButtonGroup,
  IconButton,
  Pagination,
  Button,
  Menu,
  Portal,
  Input,
  InputGroup,
  Select,
  HStack,
  Group,
} from "@chakra-ui/react";
import { LuChevronLeft, LuChevronRight, LuChevronDown } from "react-icons/lu";
import { FaSearch } from "react-icons/fa";

/**
 * headers: [{ key:'name', label:'Product', textAlign? }]
 * items: array de objetos
 * NEW (card):
 *  showCard (bool)
 *  cardTitle (string)
 *  cardBg
 *  cardBorderColor
 *  cardHeaderBg
 *  cardHeaderColor
 *  cardRadius
 *  cardBodyPadding
 */
export function GenericTable({
  // ---- Table props ----
  headers = [],
  items = [],
  sizes = ["sm"],
  caption,
  rowKey = (it) => it.id || it.key || JSON.stringify(it),
  // ---- Actions column ----
  renderActions,
  actionHeader = "Acciones",
  headerBg = "teal.700",
  headerColor = "whiteAlpha.900",
  headerBorderColor = "gray.700",
  borderColor = "blackAlpha.900",
  bodyBg = "whiteAlpha.900",
  bodyBgCell = "whiteAlpha.900",
  rowHoverBg = "purple.50",
  stripe = false,
  stripeBg = "gray.50",
  cellColor = "black",
  actionAlignment = "center",
  // ---- Card props ----
  cardTitle,
  cardBg = "white",
  cardBorderColor = "gray.200",
  cardHeaderBg = "green.700",
  cardHeaderColor = "white",
  cardRadius = "md",
  // ---- Add button props ----
  onAdd,
  // sortDir,
  // onToggleSortDir,
  // sortAscLabel = "Ascendente",
  // sortDescLabel = "Descendente",
  // showSortButton = true,
  // ---- Pagination props ----
  pagination = true,
  page = 1,
  pageSize = 10,
  totalPages = 1,
  totalElements = items.length,
  onPageChange,
  // ---- Search bar props ----
  menuItems = [],
  menuButtonText = "Open",
  searchTerm = "",
  onSearchTermChange = () => { },
  searchType = "",
  onSearchTypeChange = () => { },
}) {
  const paginatedItems =
    pagination && totalPages
      ? items
      : pagination
        ? items.slice((page - 1) * pageSize, page * pageSize)
        : items;
  const hasActions = typeof renderActions === "function";

  const TableContent = (
    <For each={sizes}>
      {(size) => (
        <Table.ScrollArea rounded="lg" key={size} height="340px">
          <Table.Root
            key={size}
            size={size}
            showColumnBorder
            borderColor={borderColor}
            bg={bodyBg}
            stickyHeader
          >
            <Table.Caption color={cellColor} mt={2}>{caption}</Table.Caption>
            <Table.Header>
              <Table.Row bg={headerBg} color={headerColor}>
                {headers.map((h) => (
                  <Table.ColumnHeader
                    key={h.key}
                    px={4}
                    py={2}
                    textAlign={h.textAlign || "start"}
                    color={headerColor}
                    borderColor={headerBorderColor}
                  >
                    {h.label}
                  </Table.ColumnHeader>
                ))}
                {hasActions && (
                  <Table.ColumnHeader
                    textAlign="center"
                    color={headerColor}
                    borderColor={headerBorderColor}
                  >
                    {actionHeader}
                  </Table.ColumnHeader>
                )}
              </Table.Row>
            </Table.Header>
            <Table.Body>
              {paginatedItems.length === 0 && (
                <Table.Row>
                  <Table.Cell
                    colSpan={headers.length + (hasActions ? 1 : 0)}
                    bg={bodyBgCell}
                  >
                    <Text fontSize="sm" color={cellColor} textAlign="center">
                      Sin registros
                    </Text>
                  </Table.Cell>
                </Table.Row>
              )}
              {paginatedItems.map((it, idx) => {
                const zebra = stripe && idx % 2 === 1;
                return (
                  <Table.Row
                    key={rowKey(it)}
                    bg={zebra ? stripeBg : bodyBgCell}
                    _hover={{ bg: rowHoverBg }}
                  >
                    {headers.map((h) => (
                      <Table.Cell
                        key={h.key}
                        px={4}
                        py={2}
                        textAlign={h.textAlign || "start"}
                        color={cellColor}
                        borderColor="gray.200"
                      >
                        {it[h.key] ?? "-"}
                      </Table.Cell>
                    ))}
                    {hasActions && (
                      <Table.Cell
                        textAlign={actionAlignment || "center"}
                        color={cellColor}
                        borderColor="gray.200"
                      >
                        {renderActions(it)}
                      </Table.Cell>
                    )}
                  </Table.Row>
                );
              })}
            </Table.Body>
          </Table.Root>
        </Table.ScrollArea>
      )}
    </For>
  );

  return (
    <Box mt={14} p={4} w="100%" h="100%" overflow="hidden">
      <Card.Root
        w="100%"
        maxW="100%"
        maxH="600px" // <-- Ajusta este valor según tu diseño
        bg={cardBg}
        borderColor={cardBorderColor}
        borderWidth="1px"
        borderRadius={cardRadius}
        boxShadow="sm"
        p={0}
      >
        {(cardTitle || caption) && (
          <Box
            as="header"
            w="100%"
            bg={cardHeaderBg}
            color={cardHeaderColor}
            fontSize="sm"
            fontWeight="bold"
            px={2}
            py={2}
            borderTopLeftRadius={cardRadius}
            borderTopRightRadius={cardRadius}
          >
            {cardTitle || caption}
          </Box>
        )}
        <Flex justify="space-between" align="center" px={6} pt={3}>
          <Group w="400px" gap={"unset"}>
            <Menu.Root>
              <Menu.Trigger asChild gap={"unset"}>
                <Button
                  variant="solid"
                  borderLeftRadius="10px"
                  borderRightRadius={0}
                  bg={headerBg}
                  color="white"
                  px={2}
                  _hover={{ bg: cardHeaderBg }}
                  focusRing={false}
                >
                  <Flex align="center" gap={2}>
                    <LuChevronDown />
                    {menuButtonText}
                  </Flex>
                </Button>
              </Menu.Trigger>
              <Portal>
                <Menu.Positioner>
                  <Menu.Content
                    borderRadius={"5px"}
                    bg={"white"}
                    boxShadow={"md"}
                    border={"1px solid"}
                    borderColor={"gray.200"}
                    px={0}
                    py={1}
                    minW="160px"
                  >
                    {menuItems.map((item) => (
                      <Menu.Item
                        key={item.value}
                        value={item.value}
                        _hover={{ bg: "gray.100" }}
                        px={4}
                        py={2}
                        fontSize="sm"
                        color="gray.800"
                        cursor="pointer"
                        onClick={() => onSearchTypeChange(item.value)} // <-- IMPORTANTE
                      >
                        {item.label}
                      </Menu.Item>
                    ))}
                  </Menu.Content>
                </Menu.Positioner>
              </Portal>
            </Menu.Root>
            <Input
              borderRadius={0}
              borderColor="gray.300"
              placeholder="Buscar ..."
              bg="white"
              fontSize="sm"
              color={"blackAlpha.800"}
              pl={2}
              value={searchTerm}
              onChange={onSearchTermChange} // <-- IMPORTANTE
            />
            <IconButton
              borderRightRadius={"10px"}
              borderLeftRadius={0}
              color="white"
              bg="teal.700"
              aria-label="Buscar"
              _hover={{ bg: cardHeaderBg }}
            >
              <FaSearch />
            </IconButton>
          </Group>
          <Button colorPalette="blue" size="sm" onClick={onAdd} px={3}>
            + Añadir
          </Button>
        </Flex>
        <Card.Body p={6}>
          {TableContent}
          {pagination && (
            <Box mt={3} display="flex" justifyContent="center">
              <Pagination.Root
                count={totalElements}
                pageSize={pageSize}
                page={page}
                onPageChange={onPageChange}
              >
                <ButtonGroup variant="ghost" size="sm" wrap="wrap">
                  <Pagination.PrevTrigger asChild>
                    <IconButton aria-label="Anterior">
                      <LuChevronLeft />
                    </IconButton>
                  </Pagination.PrevTrigger>
                  <Pagination.Items
                    render={(pageObj) => (
                      <IconButton
                        key={pageObj.value}
                        variant={{ base: "ghost", _selected: "outline" }}
                        aria-label={`Página ${pageObj.value}`}
                        color={cellColor}
                        isActive={pageObj.selected}
                        _hover={{
                          bg: headerBg,         
                          color: headerColor,      
                        }}
                      >
                        {pageObj.value}
                      </IconButton>
                    )}
                  />
                  <Pagination.NextTrigger asChild>
                    <IconButton aria-label="Siguiente">
                      <LuChevronRight />
                    </IconButton>
                  </Pagination.NextTrigger>
                </ButtonGroup>
              </Pagination.Root>
            </Box>
          )}
        </Card.Body>
      </Card.Root>
    </Box>
  );
}
