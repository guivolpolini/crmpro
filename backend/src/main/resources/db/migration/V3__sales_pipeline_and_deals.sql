-- V3__sales_pipeline_and_deals.sql
-- Sales Pipeline Stages & Commercial Deals (Opportunities)

CREATE TABLE IF NOT EXISTS pipeline_stages (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    stage_order INTEGER NOT NULL DEFAULT 0,
    color VARCHAR(30) DEFAULT '#3b82f6',
    is_won BOOLEAN NOT NULL DEFAULT FALSE,
    is_lost BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS deals (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    stage_id UUID NOT NULL REFERENCES pipeline_stages(id),
    company_id UUID REFERENCES companies(id) ON DELETE SET NULL,
    contact_id UUID REFERENCES contacts(id) ON DELETE SET NULL,
    assigned_to_id UUID REFERENCES users(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    probability INTEGER NOT NULL DEFAULT 50,
    expected_close_date DATE,
    closed_at TIMESTAMP WITH TIME ZONE,
    lost_reason VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Kanban real-time ordering and organization isolation
CREATE INDEX IF NOT EXISTS idx_pipeline_stages_org ON pipeline_stages(organization_id, stage_order);
CREATE INDEX IF NOT EXISTS idx_deals_org_stage ON deals(organization_id, stage_id);
CREATE INDEX IF NOT EXISTS idx_deals_company ON deals(company_id);
CREATE INDEX IF NOT EXISTS idx_deals_contact ON deals(contact_id);
CREATE INDEX IF NOT EXISTS idx_deals_assigned ON deals(assigned_to_id);
CREATE INDEX IF NOT EXISTS idx_deals_status ON deals(organization_id, status);
