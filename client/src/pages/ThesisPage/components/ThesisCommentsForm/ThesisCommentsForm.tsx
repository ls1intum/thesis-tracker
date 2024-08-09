import { Button, Group, Stack, Textarea } from '@mantine/core'
import { useState } from 'react'
import UploadFileModal from '../../../../components/UploadFileModal/UploadFileModal'
import { useThesisCommentsContext } from '../../../../contexts/ThesisCommentsProvider/hooks'

const ThesisCommentsForm = () => {
  const { postComment, posting } = useThesisCommentsContext()

  const [message, setMessage] = useState('')
  const [file, setFile] = useState<File>()

  const [uploadModal, setUploadModal] = useState(false)

  return (
    <Stack>
      <UploadFileModal
        title='Attach File'
        opened={uploadModal}
        onClose={() => setUploadModal(false)}
        onUpload={setFile}
      />
      <Group grow>
        <Textarea
          placeholder='Add a comment'
          value={message}
          onChange={(e) => setMessage(e.target.value)}
        />
        <Button onClick={() => setUploadModal(true)}>Attach PDF</Button>
      </Group>
      <Button
        ml='auto'
        disabled={!message}
        loading={posting}
        onClick={() => postComment(message, file)}
      >
        Post Comment
      </Button>
    </Stack>
  )
}

export default ThesisCommentsForm