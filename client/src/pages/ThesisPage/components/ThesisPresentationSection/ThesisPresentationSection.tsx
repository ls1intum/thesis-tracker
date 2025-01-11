import { ThesisState } from '../../../../requests/responses/thesis'
import { useState } from 'react'
import { Accordion, Button, Stack } from '@mantine/core'
import { checkMinimumThesisState, isThesisClosed } from '../../../../utils/thesis'
import { useLoadedThesisContext } from '../../../../providers/ThesisProvider/hooks'
import ReplacePresentationModal from '../../../../components/PresentationsTable/components/ReplacePresentationModal/ReplacePresentationModal'
import PresentationsTable from '../../../../components/PresentationsTable/PresentationsTable'

const ThesisPresentationSection = () => {
  const { thesis, access } = useLoadedThesisContext()

  const [createPresentationModal, setCreatePresentationModal] = useState(false)

  if (!checkMinimumThesisState(thesis, ThesisState.WRITING)) {
    return <></>
  }

  return (
    <Accordion variant='separated' defaultValue='open'>
      <Accordion.Item value='open'>
        <Accordion.Control>Presentation</Accordion.Control>
        <Accordion.Panel>
          <Stack>
            <ReplacePresentationModal
              thesis={thesis}
              opened={createPresentationModal}
              onClose={() => setCreatePresentationModal(false)}
            />
            <PresentationsTable
              presentations={thesis.presentations}
              theses={[thesis]}
              columns={[
                'state',
                'type',
                'location',
                'streamUrl',
                'language',
                'scheduledAt',
                'actions',
              ]}
            />
            {access.student && !isThesisClosed(thesis) && (
              <Button ml='auto' onClick={() => setCreatePresentationModal(true)}>
                Create Presentation Draft
              </Button>
            )}
          </Stack>
        </Accordion.Panel>
      </Accordion.Item>
    </Accordion>
  )
}

export default ThesisPresentationSection
