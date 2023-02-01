import {
  Button,
  Checkbox,
  FormControl,
  FormErrorMessage,
  FormLabel,
  InputGroup,
  InputRightElement,
  NumberInput,
  NumberInputField,
  VStack,
} from '@chakra-ui/react';
import { Field, Form, Formik } from 'formik';
import React from 'react';

function Settings() {
  const validate = values => {
    const errors = {};

    if (
      isNaN(values.repeatingLoglines) ||
      Number(values.repeatingLoglines) < 0 ||
      Number(values.repeatingLoglines) > 100
    ) {
      errors.repeatingLoglines = 'Must be between 0% and 100%';
    }

    return errors;
  };

  return (
    <Formik
      initialValues={{
        repeatingLoglines: '0',
        fields: {
          includeTimeStamp: true,
          includeProcessingTime: true,
          includeCurrentUserID: true,
          includeBusinessGUID: true,
          includePathToFile: true,
          includeFileSHA256: true,
          includeDisposition: true,
        },
        malware: {
          includeTrojan: false,
          includeAdware: false,
          includeRansom: false,
        },
      }}
      validate={validate}
      onSubmit={(values, actions) => {
        setTimeout(() => {
          alert(JSON.stringify(values, null, 2));
          actions.setSubmitting(false);
        }, 1000);
      }}
    >
      {props => (
        <Form>
          <VStack spacing="1.5em" align="flex-start">
            <Field name="repeatingLoglines">
              {({ field, form }) => (
                <FormControl isInvalid={form.errors.repeatingLoglines}>
                  <FormLabel>Repeating Loglines</FormLabel>
                  <InputGroup maxW="6em">
                    <NumberInput
                      min={0}
                      max={100}
                      onChange={val => form.setFieldValue(field.name, val)}
                    >
                      <NumberInputField placeholder="0" />
                      <InputRightElement
                        pointerEvents="none"
                        color="gray.300"
                        fontSize="1.2em"
                        children="%"
                      />
                    </NumberInput>
                  </InputGroup>
                  <FormErrorMessage>
                    {form.errors.repeatingLoglines}
                  </FormErrorMessage>
                </FormControl>
              )}
            </Field>
            <FormControl>
              <FormLabel>Include Fields:</FormLabel>
              <VStack spacing="0.75em" align="flex-start">
                <Field
                  as={Checkbox}
                  name="fields.includeTimeStamp"
                  defaultChecked
                >
                  Time stamp
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeProcessingTime"
                  defaultChecked
                >
                  Processing time
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeCurrentUserID"
                  defaultChecked
                >
                  Current user ID
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeBusinessGUID"
                  defaultChecked
                >
                  Business GUID
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includePathToFile"
                  defaultChecked
                >
                  Path to file
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeFileSHA256"
                  defaultChecked
                >
                  File SHA256
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeDisposition"
                  defaultChecked
                >
                  Disposition
                </Field>
              </VStack>
            </FormControl>
            <FormControl>
              <FormLabel>Include Malware:</FormLabel>
              <VStack spacing="0.75em" align="flex-start">
                <Field as={Checkbox} name="malware.includeTrojan">
                  Trojan
                </Field>
                <Field as={Checkbox} name="malware.includeAdware">
                  Adware
                </Field>
                <Field as={Checkbox} name="malware.includeRansom">
                  Ransom
                </Field>
              </VStack>
            </FormControl>
            <Button
              mt={4}
              colorScheme="teal"
              isLoading={props.isSubmitting}
              type="submit"
            >
              Start
            </Button>
          </VStack>
        </Form>
      )}
    </Formik>
  );
}

export default Settings;
