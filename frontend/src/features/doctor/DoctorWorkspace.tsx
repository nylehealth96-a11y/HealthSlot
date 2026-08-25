import { RoleWorkspace } from '../../components/RoleWorkspace'
import { capabilities } from '../../api/capabilities'

export function DoctorWorkspace() { return <RoleWorkspace title="Doctor"><p>Doctor workflows</p><p role="note">{capabilities.doctorQueue.explanation}</p><button disabled>Today's queue</button><button disabled>Call patient</button><button disabled>Start consultation</button><button disabled>Complete consultation</button></RoleWorkspace> }
