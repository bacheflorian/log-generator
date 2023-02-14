import { Flex, Text } from '@chakra-ui/react';
import React from 'react';
import { Link } from 'react-router-dom';
import { ColorModeSwitcher } from './ColorModeSwitcher';

function Navbar() {
  return (
    <Flex align="center" justify="space-between" p="1em 4%" bg="#00BCEB">
      <Link to="/">
        <Text
          fontSize="1.5em"
          fontWeight="bold"
          color="#fff"
          sx={{
            '&:hover': {
              cursor: 'pointer',
            },
          }}
        >
          Log Generator
        </Text>
      </Link>
      <ColorModeSwitcher />
    </Flex>
  );
}

export default Navbar;
