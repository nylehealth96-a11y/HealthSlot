import { RoleWorkspace } from '../../components/RoleWorkspace'
import { capabilities } from '../../api/capabilities'

export function ReceptionWorkspace() { return <RoleWorkspace title="Reception / Admin"><p>Reception workflows</p><p role="note">{capabilities.patientRegistration.explanation}</p><button disabled>Register patient</button><button disabled>Book appointment</button><button disabled>Check in</button><button disabled>Add walk-in</button></RoleWorkspace> }
