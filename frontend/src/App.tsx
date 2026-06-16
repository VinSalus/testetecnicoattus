import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'

type Prioridade = 'BAIXA' | 'MEDIA' | 'ALTA'
type StatusDemanda = 'ABERTA' | 'EM_ANDAMENTO' | 'CONCLUIDA'

type Demanda = {
  id: number
  titulo: string
  descricao: string
  prioridade: Prioridade
  status: StatusDemanda
  responsavel: string
  prazo: string
}

type FormState = Omit<Demanda, 'id'>

const emptyForm: FormState = {
  titulo: '',
  descricao: '',
  prioridade: 'MEDIA',
  status: 'ABERTA',
  responsavel: '',
  prazo: new Date().toISOString().slice(0, 10),
}

const statusLabel: Record<StatusDemanda, string> = {
  ABERTA: 'Aberta',
  EM_ANDAMENTO: 'Em andamento',
  CONCLUIDA: 'Concluida',
}

export default function App() {
  const [demandas, setDemandas] = useState<Demanda[]>([])
  const [form, setForm] = useState<FormState>(emptyForm)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const today = useMemo(() => new Date().toISOString().slice(0, 10), [])

  useEffect(() => {
    loadDemandas()
  }, [])

  async function loadDemandas() {
    const response = await fetch('/api/demandas')
    setDemandas(await response.json())
  }

  function validate() {
    if (!form.titulo.trim() || !form.descricao.trim() || !form.responsavel.trim()) {
      return 'Preencha titulo, descricao e responsavel.'
    }
    if (form.prazo < today) {
      return 'O prazo nao pode estar no passado.'
    }
    return ''
  }

  async function save(event: FormEvent) {
    event.preventDefault()
    const validation = validate()
    if (validation) {
      setError(validation)
      return
    }

    setLoading(true)
    setError('')
    const response = await fetch(editingId ? `/api/demandas/${editingId}` : '/api/demandas', {
      method: editingId ? 'PUT' : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form),
    })

    if (!response.ok) {
      setError('Nao foi possivel salvar a demanda. Revise os campos e tente novamente.')
      setLoading(false)
      return
    }

    setForm(emptyForm)
    setEditingId(null)
    await loadDemandas()
    setLoading(false)
  }

  async function conclude(id: number) {
    await fetch(`/api/demandas/${id}/concluir`, { method: 'PATCH' })
    await loadDemandas()
  }

  function edit(demanda: Demanda) {
    setEditingId(demanda.id)
    setForm({
      titulo: demanda.titulo,
      descricao: demanda.descricao,
      prioridade: demanda.prioridade,
      status: demanda.status,
      responsavel: demanda.responsavel,
      prazo: demanda.prazo,
    })
    setError('')
  }

  return (
    <main className="shell">
      <section className="panel form-panel">
        <div>
          <p className="eyebrow">Modulo de demandas</p>
          <h1>Gestao de demandas juridicas</h1>
        </div>

        <form onSubmit={save} noValidate>
          <label>
            Titulo
            <input value={form.titulo} maxLength={120} onChange={(event) => setForm({ ...form, titulo: event.target.value })} />
          </label>

          <label>
            Descricao
            <textarea value={form.descricao} maxLength={1000} rows={4} onChange={(event) => setForm({ ...form, descricao: event.target.value })} />
          </label>

          <div className="grid">
            <label>
              Prioridade
              <select value={form.prioridade} onChange={(event) => setForm({ ...form, prioridade: event.target.value as Prioridade })}>
                <option value="BAIXA">Baixa</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
              </select>
            </label>

            <label>
              Status
              <select value={form.status} onChange={(event) => setForm({ ...form, status: event.target.value as StatusDemanda })}>
                <option value="ABERTA">Aberta</option>
                <option value="EM_ANDAMENTO">Em andamento</option>
                <option value="CONCLUIDA">Concluida</option>
              </select>
            </label>
          </div>

          <div className="grid">
            <label>
              Responsavel
              <input value={form.responsavel} maxLength={80} onChange={(event) => setForm({ ...form, responsavel: event.target.value })} />
            </label>

            <label>
              Prazo
              <input type="date" min={today} value={form.prazo} onChange={(event) => setForm({ ...form, prazo: event.target.value })} />
            </label>
          </div>

          {error && <p className="error">{error}</p>}

          <div className="actions">
            <button type="submit" disabled={loading}>{editingId ? 'Salvar edicao' : 'Criar demanda'}</button>
            {editingId && <button type="button" className="ghost" onClick={() => { setEditingId(null); setForm(emptyForm); }}>Cancelar</button>}
          </div>
        </form>
      </section>

      <section className="panel">
        <div className="list-header">
          <h2>Demandas</h2>
          <span>{demandas.length} registro(s)</span>
        </div>

        <div className="list">
          {demandas.length === 0 && <p className="empty">Nenhuma demanda cadastrada.</p>}
          {demandas.map((demanda) => (
            <article className="card" key={demanda.id}>
              <div>
                <strong>{demanda.titulo}</strong>
                <p>{demanda.descricao}</p>
              </div>
              <div className="meta">
                <span>{demanda.prioridade}</span>
                <span>{statusLabel[demanda.status]}</span>
                <span>{demanda.responsavel}</span>
                <span>{new Date(`${demanda.prazo}T00:00:00`).toLocaleDateString('pt-BR')}</span>
              </div>
              <div className="actions">
                <button type="button" className="ghost" onClick={() => edit(demanda)}>Editar</button>
                <button type="button" disabled={demanda.status === 'CONCLUIDA'} onClick={() => conclude(demanda.id)}>Concluir</button>
              </div>
            </article>
          ))}
        </div>
      </section>
    </main>
  )
}
