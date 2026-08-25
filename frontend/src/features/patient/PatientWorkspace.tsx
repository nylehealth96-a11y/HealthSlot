import { RoleWorkspace } from '../../components/RoleWorkspace'
import { capabilities } from '../../api/capabilities'

export function PatientWorkspace() { return <RoleWorkspace title="Patient"><p>Patient workflows</p><p role="note">{capabilities.patientSearch.explanation}</p><button disabled>Search doctors</button><button disabled>View slots</button><button disabled>Book appointment</button><button disabled>Check ETA</button></RoleWorkspace> }
