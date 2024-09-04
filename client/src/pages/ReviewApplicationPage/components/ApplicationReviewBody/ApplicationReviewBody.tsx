import ApplicationData from '../../../../components/ApplicationData/ApplicationData'
import ApplicationReviewForm from '../../../../components/ApplicationReviewForm/ApplicationReviewForm'
import { Divider, Stack } from '@mantine/core'
import React, { useEffect } from 'react'
import { IApplication } from '../../../../requests/responses/application'
import { useWindowScroll } from '@mantine/hooks'
import ApplicationRejectButton from '../../../../components/ApplicationRejectButton/ApplicationRejectButton'
import { useApplicationsContext } from '../../../../contexts/ApplicationsProvider/hooks'

interface IApplicationReviewBodyProps {
  application: IApplication
  onChange: (application: IApplication) => unknown
}

const ApplicationReviewBody = (props: IApplicationReviewBodyProps) => {
  const { application, onChange } = props

  const [, scrollTo] = useWindowScroll()

  useEffect(() => {
    scrollTo({ y: 0 })
  }, [application.applicationId])

  return (
    <Stack>
      <ApplicationData
        application={application}
        rightTitleSection={
          <ApplicationRejectButton
            application={application}
            onUpdate={(newApplication) => {
              onChange(newApplication)
            }}
            ml='auto'
          />
        }
        bottomSection={
          <Stack>
            <Divider />
            <ApplicationReviewForm
              application={application}
              onUpdate={(newApplication) => {
                onChange(newApplication)
              }}
            />
          </Stack>
        }
      />
    </Stack>
  )
}

export default ApplicationReviewBody
