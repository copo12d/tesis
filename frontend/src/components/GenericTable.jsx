import React from "react";
import { Stack, Table, For, Text, Box, Flex, Card, ButtonGroup, IconButton, Pagination , Button} from "@chakra-ui/react";
import { LuChevronLeft, LuChevronRight, LuCirclePlus } from "react-icons/lu";
import { FaSortAlphaDown, FaSortAlphaUp } from "react-icons/fa";


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
  headerBg = "gray.800",
  headerColor = "whiteAlpha.900",
  headerBorderColor = "gray.700",
  borderColor = "blackAlpha.900",
  bodyBg = "whiteAlpha.900",
  bodyBgCell = "whiteAlpha.900",
  rowHoverBg = "purple.50",
  stripe = false,
  stripeBg = "gray.50",
  cellColor = "blackAlpha.800",
  actionAlignment="center",
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
}) {

  const paginatedItems = pagination && totalPages
    ? items
    : pagination
      ? items.slice((page - 1) * pageSize, page * pageSize)
      : items;
  const hasActions = typeof renderActions === "function";

  const TableContent = (
    <For each={sizes}>
      {(size) => (
        <Table.ScrollArea rounded="md" key={size}>
          <Table.Root
            key={size}
            size={size}
            showColumnBorder
            borderColor={borderColor}
            bg={bodyBg}
          >
            <Table.Caption color="blackAlpha.600">{caption}</Table.Caption>
            <Table.Header>
              <Table.Row bg={headerBg} color={headerColor} >
                {headers.map((h) => (
                  <Table.ColumnHeader
                    key={h.key}
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
                    <Text fontSize="sm" color="gray.500" textAlign="center">
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
      <Box p={4} w="100%" h="100%">
        <Card.Root
          w="100%"
          maxW="100%"
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
          <Flex justify="flex-end" align="center" px={6} pt={3}>
            <Button
              colorPalette="blue"
              size="sm"
              onClick={onAdd}
              px={3}
            >
             + Añadir
            </Button>
          </Flex>
          <Card.Body p={3}>
            {TableContent}
            {pagination && (
              <Box mt={3} display="flex" justifyContent="center" >
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
                          isActive={pageObj.selected}
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