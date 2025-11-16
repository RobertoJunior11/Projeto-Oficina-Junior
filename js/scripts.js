// URLs das APIs
const PROPRIETARIO_API_URL = "http://localhost:8080/proprietario";
const VEICULO_API_URL = "http://localhost:8080/veiculo";
const REVISAO_API_URL = "http://localhost:8080/revisao";

// Variáveis de paginação
let paginaProprietarios = 0;
let paginaVeiculos = 0;
let paginaRevisoes = 0;

// Tipos de revisão (hardcoded por enquanto)
const TIPOS_REVISAO = [
  { id: 1, descricao: "Troca de Óleo", valor: 120.0 },
  { id: 2, descricao: "Alinhamento e Balanceamento", valor: 150.0 },
  { id: 3, descricao: "Troca de Pneu", valor: 250.0 },
  { id: 4, descricao: "Revisão Completa (óleo, filtros, velas)", valor: 400.0 },
  { id: 5, descricao: "Troca de Filtro de Ar", valor: 50.0 },
  { id: 6, descricao: "Troca de Filtro de Óleo", valor: 60.0 },
  { id: 7, descricao: "Troca de Bateria", valor: 350.0 },
  { id: 8, descricao: "Troca de Velas de Ignição", valor: 80.0 },
  { id: 9, descricao: "Troca de Limpador de Parabrisa", valor: 30.0 },
  { id: 10, descricao: "Inspeção Técnica", valor: 100.0 },
];

// Função para alternar entre abas
function showTab(tabName) {
  // Esconder todas as abas
  const tabs = document.querySelectorAll(".tab-content");
  tabs.forEach((tab) => tab.classList.remove("active"));

  // Remover classe active de todos os botões
  const buttons = document.querySelectorAll(".tab-button");
  buttons.forEach((button) => button.classList.remove("active"));

  // Mostrar a aba selecionada
  document.getElementById(tabName).classList.add("active");

  // Adicionar classe active ao botão clicado
  event.target.classList.add("active");

  // Carregar dados da aba selecionada
  if (tabName === "proprietarios") {
    carregarProprietarios();
  } else if (tabName === "veiculos") {
    carregarVeiculos();
    carregarProprietariosParaVeiculo();
  } else if (tabName === "revisoes") {
    carregarRevisoes();
    carregarVeiculosParaRevisao();
    inicializarTiposRevisao();
  } else if (tabName === "agendamentos") {
    carregarAgendamentos();
    carregarRevisoesParaAgendamento();
  }
}

// ==================== FUNCIONALIDADES DE PROPRIETÁRIOS ====================

// Função para carregar proprietários no select de veículos
async function carregarProprietariosParaVeiculo() {
  try {
    const resposta = await fetch(`${PROPRIETARIO_API_URL}?page=0&size=1000`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar proprietários");
    }

    const dados = await resposta.json();
    const proprietarios = dados.content;
    const select = document.getElementById("proprietarioVeiculo");

    // Limpar opções existentes (exceto a primeira)
    select.innerHTML = '<option value="">Selecione um proprietário</option>';

    proprietarios.forEach((proprietario) => {
      const option = document.createElement("option");
      option.value = proprietario.id;
      option.textContent = `${proprietario.nome} - ${proprietario.email}`;
      select.appendChild(option);
    });
  } catch (erro) {
    console.error("Erro ao carregar proprietários:", erro);
  }
}

document.getElementById("proprietarioForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const id = document.getElementById("proprietarioId").value;
  const nome = document.getElementById("nome").value;
  const email = document.getElementById("email").value;
  const telefone = document.getElementById("telefone").value;

  let dados = { id: parseInt(id), nome, email, telefone };

  let metodo = "POST";
  let url = PROPRIETARIO_API_URL;

  if (id) {
    metodo = "PUT";
    url = `${PROPRIETARIO_API_URL}/${id}`;
  }

  try {
    const resposta = await fetch(url, {
      method: metodo,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(dados),
    });

    if (!resposta.ok) {
      throw new Error("Erro ao salvar o proprietário");
    }

    resetarFormularioProprietario();
    carregarProprietarios();
  } catch (erro) {
    alert(`Erro: ${erro.message}`);
  }
});

function resetarFormularioProprietario() {
  document.getElementById("proprietarioId").value = "";
  document.getElementById("nome").value = "";
  document.getElementById("email").value = "";
  document.getElementById("telefone").value = "";
}

async function carregarProprietarios() {
  try {
    const resposta = await fetch(`${PROPRIETARIO_API_URL}?page=${paginaProprietarios}&size=5`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar os proprietários");
    }

    const dados = await resposta.json();
    const proprietarios = dados.content;

    const tabela = document.getElementById("tabelaProprietarios");
    tabela.innerHTML = "";

    proprietarios.forEach((p) => {
      const linha = document.createElement("tr");
      const veiculosInfo =
        p.veiculos && p.veiculos.length > 0 ? p.veiculos.map((v) => `${v.veiculo} (${v.marca})`).join(", ") : "Nenhum veículo";

      linha.innerHTML = `
                <td>${p.id}</td>
                <td>${p.nome}</td>
                <td>${p.email}</td>
                <td>${p.telefone}</td>
                <td>${veiculosInfo}</td>
                <td>
                    <button onclick="editarProprietario(${p.id})">Editar</button>
                    <button onclick="deletarProprietario(${p.id})">Excluir</button>
                </td>
            `;
      tabela.appendChild(linha);
    });

    document.getElementById("paginaAtualProprietarios").innerText = paginaProprietarios + 1;
  } catch (erro) {
    alert(`Erro ao carregar proprietários: ${erro.message}`);
  }
}

async function editarProprietario(id) {
  try {
    const resposta = await fetch(`${PROPRIETARIO_API_URL}/${id}`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar proprietário");
    }

    const proprietario = await resposta.json();
    document.getElementById("proprietarioId").value = proprietario.id;
    document.getElementById("nome").value = proprietario.nome;
    document.getElementById("email").value = proprietario.email;
    document.getElementById("telefone").value = proprietario.telefone;
  } catch (erro) {
    alert(`Erro ao editar proprietário: ${erro.message}`);
  }
}

async function deletarProprietario(id) {
  if (confirm("Tem certeza que deseja excluir este proprietário?")) {
    try {
      const resposta = await fetch(`${PROPRIETARIO_API_URL}/${id}`, { method: "DELETE" });
      if (!resposta.ok) {
        throw new Error("Erro ao excluir proprietário");
      }

      carregarProprietarios();
    } catch (erro) {
      alert(`Erro ao excluir proprietário: ${erro.message}`);
    }
  }
}

// Event listeners para paginação de proprietários
document.getElementById("anteriorProprietarios").addEventListener("click", () => {
  if (paginaProprietarios > 0) {
    paginaProprietarios--;
    carregarProprietarios();
  }
});

document.getElementById("proximoProprietarios").addEventListener("click", () => {
  paginaProprietarios++;
  carregarProprietarios();
});

// ==================== FUNCIONALIDADES DE VEÍCULOS ====================

// Carregar todos os veículos na aba de proprietários

document.getElementById("veiculoForm").addEventListener("submit", async function (e) {
  e.preventDefault(); // Evita o envio padrão do formulário

  const id = document.getElementById("veiculoId").value;
  const veiculo = document.getElementById("veiculo").value;
  const marca = document.getElementById("marca").value;
  const ano = parseInt(document.getElementById("ano").value);
  const proprietarioId = document.getElementById("proprietarioVeiculo").value;

  let dados = { id: parseInt(id), veiculo, marca, ano, proprietario: parseInt(proprietarioId) };

  let metodo = "POST"; // Default para cadastrar
  let url = VEICULO_API_URL;

  if (id) {
    metodo = "PUT"; // Se ID existe, a operação é de atualização
    url = `${VEICULO_API_URL}/${id}`;
  }

  try {
    const resposta = await fetch(url, {
      method: metodo,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(dados),
    });

    if (!resposta.ok) {
      const erro = await resposta.json();
      throw new Error(erro.message || "Erro ao salvar o veículo");
    }

    const resultado = await resposta.json();
    console.log("Veículo salvo:", resultado);

    resetarFormulario();
    carregarVeiculos(); // Atualiza a lista de veículos
    alert("Veículo salvo com sucesso!");
  } catch (erro) {
    alert(`Erro: ${erro.message}`);
  }
});

function resetarFormulario() {
  document.getElementById("veiculoId").value = "";
  document.getElementById("veiculo").value = "";
  document.getElementById("marca").value = "";
  document.getElementById("ano").value = "";
  document.getElementById("proprietarioVeiculo").value = "";
}

async function carregarVeiculos() {
  try {
    const resposta = await fetch(`${VEICULO_API_URL}?page=${paginaVeiculos}&size=5`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar os veículos");
    }

    const dados = await resposta.json();
    const veiculos = dados.content;

    const tabela = document.getElementById("tabelaVeiculos");
    tabela.innerHTML = "";

    if (veiculos.length === 0) {
      const linha = document.createElement("tr");
      linha.innerHTML = `
        <td colspan="6" style="text-align: center; padding: 20px; color: #666;">
          Nenhum veículo cadastrado
        </td>
      `;
      tabela.appendChild(linha);
    } else {
      veiculos.forEach((v) => {
        const linha = document.createElement("tr");
        linha.innerHTML = `
                  <td>${v.id}</td>
                  <td>${v.veiculo}</td>
                  <td>${v.marca}</td>
                  <td>${v.ano}</td>
                  <td>${v.proprietario ? v.proprietario.nome : "N/A"}</td>
                  <td>
                      <button onclick="editarVeiculo(${v.id})">Editar</button>
                      <button onclick="deletarVeiculo(${v.id})">Excluir</button>
                  </td>
              `;
        tabela.appendChild(linha);
      });
    }

    document.getElementById("paginaAtualVeiculos").innerText = paginaVeiculos + 1;
  } catch (erro) {
    alert(`Erro ao carregar veículos: ${erro.message}`);
  }
}

async function editarVeiculo(id) {
  try {
    const resposta = await fetch(`${VEICULO_API_URL}/${id}`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar veículo");
    }

    const veiculo = await resposta.json();
    document.getElementById("veiculoId").value = veiculo.id;
    document.getElementById("veiculo").value = veiculo.veiculo;
    document.getElementById("marca").value = veiculo.marca;
    document.getElementById("ano").value = veiculo.ano;
    document.getElementById("proprietarioVeiculo").value = veiculo.proprietario ? veiculo.proprietario.id : "";

    // Scroll para o formulário
    document.getElementById("veiculoForm").scrollIntoView({ behavior: "smooth" });
  } catch (erro) {
    alert(`Erro ao editar veículo: ${erro.message}`);
  }
}

async function deletarVeiculo(id) {
  if (confirm("Tem certeza que deseja excluir este veículo?")) {
    try {
      const resposta = await fetch(`${VEICULO_API_URL}/${id}`, { method: "DELETE" });
      if (!resposta.ok) {
        const erro = await resposta.json();
        throw new Error(erro.message || "Erro ao excluir veículo");
      }

      carregarVeiculos(); // Atualiza a lista após exclusão
      alert("Veículo excluído com sucesso!");
    } catch (erro) {
      alert(`Erro ao excluir veículo: ${erro.message}`);
    }
  }
}

// Event listeners para paginação de veículos
document.getElementById("anteriorVeiculos").addEventListener("click", () => {
  if (paginaVeiculos > 0) {
    paginaVeiculos--;
    carregarVeiculos();
  }
});

document.getElementById("proximoVeiculos").addEventListener("click", () => {
  paginaVeiculos++;
  carregarVeiculos();
});

// ==================== FUNCIONALIDADES DE REVISÕES ====================

// Função para carregar veículos no select de revisões
async function carregarVeiculosParaRevisao() {
  try {
    const resposta = await fetch(`${VEICULO_API_URL}?page=0&size=1000`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar veículos");
    }

    const dados = await resposta.json();
    const veiculos = dados.content;
    const select = document.getElementById("veiculoRevisao");

    // Limpar opções existentes (exceto a primeira)
    select.innerHTML = '<option value="">Selecione um veículo</option>';

    veiculos.forEach((veiculo) => {
      const option = document.createElement("option");
      option.value = veiculo.id;
      option.textContent = `${veiculo.veiculo} - ${veiculo.marca} (${veiculo.ano})`;
      select.appendChild(option);
    });
  } catch (erro) {
    console.error("Erro ao carregar veículos:", erro);
  }
}

// Função para inicializar tipos de revisão
function inicializarTiposRevisao() {
  const container = document.getElementById("tiposRevisaoContainer");
  container.innerHTML = "";

  TIPOS_REVISAO.forEach((tipo) => {
    const div = document.createElement("div");
    div.className = "tipo-revisao-item";
    div.innerHTML = `
      <input type="checkbox" id="tipo-${tipo.id}" value="${tipo.id}">
      <label for="tipo-${tipo.id}">${tipo.descricao}</label>
      <span class="valor">R$ ${tipo.valor.toFixed(2)}</span>
    `;
    container.appendChild(div);
  });
}

// Função para coletar tipos de revisão selecionados
function coletarTiposRevisaoSelecionados() {
  const checkboxes = document.querySelectorAll('#tiposRevisaoContainer input[type="checkbox"]:checked');
  return Array.from(checkboxes).map((cb) => parseInt(cb.value));
}

// Função para calcular valor total dos tipos selecionados
function calcularValorTotal() {
  const tiposSelecionados = coletarTiposRevisaoSelecionados();
  return tiposSelecionados.reduce((total, tipoId) => {
    const tipo = TIPOS_REVISAO.find((t) => t.id === tipoId);
    return total + (tipo ? tipo.valor : 0);
  }, 0);
}

// Event listener para o formulário de revisões
document.getElementById("revisaoForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const id = document.getElementById("revisaoId").value;
  const veiculoId = document.getElementById("veiculoRevisao").value;
  const descricao = document.getElementById("descricaoRevisao").value;
  const tiposSelecionados = coletarTiposRevisaoSelecionados();
  const concluida = document.getElementById("concluidaRevisao").checked;

  if (tiposSelecionados.length === 0) {
    alert("Selecione pelo menos um tipo de revisão");
    return;
  }

  const revisaoData = {
    id: parseInt(id) || undefined,
    descricao: descricao,
    dtRevisao: window.dataRevisaoEditando || null,
    tiposRevisao: tiposSelecionados,
    concluida: concluida,
    veiculo: { id: parseInt(veiculoId) },
  };

  try {
    let resposta;
    if (id) {
      // Atualizar revisão existente
      resposta = await fetch(`${REVISAO_API_URL}/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(revisaoData),
      });
    } else {
      // Criar nova revisão
      resposta = await fetch(REVISAO_API_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(revisaoData),
      });
    }

    if (!resposta.ok) {
      const erro = await resposta.json();
      throw new Error(erro.message || "Erro ao salvar revisão");
    }

    alert("Revisão salva com sucesso!");
    resetarFormularioRevisao();
    carregarRevisoes();
  } catch (erro) {
    alert(`Erro ao salvar revisão: ${erro.message}`);
  }
});

// Função para resetar formulário de revisões
function resetarFormularioRevisao() {
  document.getElementById("revisaoId").value = "";
  document.getElementById("veiculoRevisao").value = "";
  document.getElementById("descricaoRevisao").value = "";
  document.getElementById("concluidaRevisao").checked = false;

  // Limpar data armazenada em memória
  window.dataRevisaoEditando = null;

  // Desmarcar todos os checkboxes
  const checkboxes = document.querySelectorAll('#tiposRevisaoContainer input[type="checkbox"]');
  checkboxes.forEach((cb) => (cb.checked = false));
}

// Função para carregar revisões
async function carregarRevisoes() {
  try {
    const resposta = await fetch(`${REVISAO_API_URL}?page=${paginaRevisoes}&size=5`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar revisões");
    }

    const dados = await resposta.json();
    const revisoes = dados.content;

    const tabela = document.getElementById("tabelaRevisoes");
    tabela.innerHTML = "";

    if (revisoes.length === 0) {
      const linha = document.createElement("tr");
      linha.innerHTML = `
        <td colspan="8" style="text-align: center; padding: 20px; color: #666;">
          Nenhuma revisão cadastrada
        </td>
      `;
      tabela.appendChild(linha);
    } else {
      revisoes.forEach((revisao) => {
        const linha = document.createElement("tr");
        const tiposTexto = revisao.tiposRevisao ? revisao.tiposRevisao.map((t) => t.descricao).join(", ") : "N/A";
        const valorTotal = revisao.vlServico ? `R$ ${revisao.vlServico.toFixed(2)}` : "N/A";
        const concluida = revisao.concluida ? "Concluído" : "Pendente";
        const statusClass = revisao.concluida ? "status-concluida" : "status-pendente";

        // Formatar data para exibição
        let dataFormatada = "N/A";
        if (revisao.dtRevisao) {
          const data = new Date(revisao.dtRevisao);
          dataFormatada = data.toLocaleString("pt-BR", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
          });
        }

        linha.innerHTML = `
          <td>${revisao.id}</td>
          <td>${revisao.veiculo ? `${revisao.veiculo.veiculo} - ${revisao.veiculo.marca}` : "N/A"}</td>
          <td>${dataFormatada}</td>
          <td>${revisao.descricao || "N/A"}</td>
          <td>${tiposTexto}</td>
          <td>${valorTotal}</td>
          <td class="${statusClass}">${concluida}</td>
          <td>
            <button onclick="editarRevisao(${revisao.id})">Editar / Finalizar</button>
            <button onclick="deletarRevisao(${revisao.id})">Excluir</button>
          </td>
        `;
        tabela.appendChild(linha);
      });
    }

    document.getElementById("paginaAtualRevisoes").innerText = paginaRevisoes + 1;
  } catch (erro) {
    alert(`Erro ao carregar revisões: ${erro.message}`);
  }
}

// Função para editar revisão
async function editarRevisao(id) {
  try {
    const resposta = await fetch(`${REVISAO_API_URL}/${id}`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar revisão");
    }

    const revisao = await resposta.json();
    document.getElementById("revisaoId").value = revisao.id;
    document.getElementById("veiculoRevisao").value = revisao.veiculo ? revisao.veiculo.id : "";
    document.getElementById("descricaoRevisao").value = revisao.descricao || "";
    document.getElementById("concluidaRevisao").checked = revisao.concluida || false;

    // Armazenar data da revisão em memória (não exibir no formulário)
    window.dataRevisaoEditando = revisao.dtRevisao || null;

    // Marcar tipos selecionados
    const checkboxes = document.querySelectorAll('#tiposRevisaoContainer input[type="checkbox"]');
    checkboxes.forEach((cb) => (cb.checked = false));

    if (revisao.tiposRevisao) {
      revisao.tiposRevisao.forEach((tipo) => {
        const checkbox = document.getElementById(`tipo-${tipo.codigo}`);
        if (checkbox) checkbox.checked = true;
      });
    }

    // Scroll para o formulário
    document.getElementById("revisaoForm").scrollIntoView({ behavior: "smooth" });
  } catch (erro) {
    alert(`Erro ao editar revisão: ${erro.message}`);
  }
}

// Função para deletar revisão
async function deletarRevisao(id) {
  if (!confirm("Tem certeza que deseja excluir esta revisão?")) {
    return;
  }

  try {
    const resposta = await fetch(`${REVISAO_API_URL}/${id}`, {
      method: "DELETE",
    });

    if (!resposta.ok) {
      throw new Error("Erro ao excluir revisão");
    }

    carregarRevisoes();
    alert("Revisão excluída com sucesso!");
  } catch (erro) {
    alert(`Erro ao excluir revisão: ${erro.message}`);
  }
}

// Event listeners para paginação de revisões
document.getElementById("anteriorRevisoes").addEventListener("click", () => {
  if (paginaRevisoes > 0) {
    paginaRevisoes--;
    carregarRevisoes();
  }
});

document.getElementById("proximoRevisoes").addEventListener("click", () => {
  paginaRevisoes++;
  carregarRevisoes();
});

// Inicialização
carregarProprietarios();

// ==================== FUNCIONALIDADES DE AGENDAMENTOS ====================

// Função para carregar revisões disponíveis para agendamento
async function carregarRevisoesParaAgendamento() {
  try {
    const resposta = await fetch(`${REVISAO_API_URL}?page=0&size=1000`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar revisões");
    }

    const dados = await resposta.json();
    const revisoes = dados.content;
    const select = document.getElementById("revisaoAgendamento");

    // Limpar opções existentes (exceto a primeira)
    select.innerHTML = '<option value="">Selecione uma revisão</option>';

    revisoes.forEach((revisao) => {
      if (revisao.veiculo) {
        const option = document.createElement("option");
        option.value = revisao.id;
        option.textContent = `${revisao.veiculo.veiculo} - ${revisao.veiculo.marca} (${revisao.descricao || "Sem descrição"})`;
        select.appendChild(option);
      }
    });
  } catch (erro) {
    console.error("Erro ao carregar revisões para agendamento:", erro);
  }
}

// Event listener para o formulário de agendamentos
document.getElementById("agendamentoForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const revisaoId = document.getElementById("revisaoAgendamento").value;
  const dataAgendamento = document.getElementById("dataAgendamento").value;

  if (!revisaoId || !dataAgendamento) {
    alert("Por favor, preencha todos os campos");
    return;
  }

  // Validar se a data não é no passado
  const dataSelecionada = new Date(dataAgendamento);
  const agora = new Date();

  if (dataSelecionada <= agora) {
    alert("A data do agendamento deve ser futura");
    return;
  }

  try {
    // Atualizar a revisão com a data de agendamento
    const resposta = await fetch(`${REVISAO_API_URL}/${revisaoId}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        dtRevisao: dataAgendamento,
      }),
    });

    if (!resposta.ok) {
      const erro = await resposta.json();
      throw new Error(erro.message || "Erro ao agendar revisão");
    }

    alert("Agendamento realizado com sucesso!");
    resetarFormularioAgendamento();
    carregarAgendamentos();
  } catch (erro) {
    alert(`Erro ao agendar revisão: ${erro.message}`);
  }
});

// Função para resetar formulário de agendamentos
function resetarFormularioAgendamento() {
  document.getElementById("revisaoAgendamento").value = "";
  document.getElementById("dataAgendamento").value = "";
}

// Função para carregar agendamentos
async function carregarAgendamentos() {
  try {
    const resposta = await fetch(`${REVISAO_API_URL}?page=0&size=1000`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar agendamentos");
    }

    const dados = await resposta.json();
    const revisoes = dados.content;

    const tabela = document.getElementById("tabelaAgendamentos");
    tabela.innerHTML = "";

    // Filtrar apenas revisões com data agendada
    const revisoesAgendadas = revisoes.filter((revisao) => revisao.dtRevisao);

    if (revisoesAgendadas.length === 0) {
      const linha = document.createElement("tr");
      linha.innerHTML = `
        <td colspan="2" style="text-align: center; padding: 20px; color: #666;">
          Nenhum agendamento encontrado
        </td>
      `;
      tabela.appendChild(linha);
    } else {
      // Ordenar por data de agendamento
      revisoesAgendadas.sort((a, b) => new Date(a.dtRevisao) - new Date(b.dtRevisao));

      revisoesAgendadas.forEach((revisao) => {
        if (revisao.veiculo) {
          const linha = document.createElement("tr");
          const data = new Date(revisao.dtRevisao);
          const dataFormatada = data.toLocaleString("pt-BR", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
          });

          const statusClass = revisao.concluida ? "status-concluida" : "status-pendente";
          const status = revisao.concluida ? "Concluído" : "Pendente";

          // Botão de editar desabilitado quando concluído
          const botaoEditar = revisao.concluida
            ? `<button onclick="editarAgendamento(${revisao.id})" disabled style="opacity: 0.5; cursor: not-allowed;">Editar</button>`
            : `<button onclick="editarAgendamento(${revisao.id})">Editar</button>`;

          linha.innerHTML = `
            <td>${revisao.veiculo.veiculo} - ${revisao.veiculo.marca}</td>
            <td>${dataFormatada}</td>
            <td class="${statusClass}">${status}</td>
            <td>
              ${botaoEditar}
            </td>
          `;
          tabela.appendChild(linha);
        }
      });
    }
  } catch (erro) {
    alert(`Erro ao carregar agendamentos: ${erro.message}`);
  }
}

// Função para editar agendamento
async function editarAgendamento(id) {
  try {
    const resposta = await fetch(`${REVISAO_API_URL}/${id}`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar revisão");
    }

    const revisao = await resposta.json();
    document.getElementById("revisaoAgendamento").value = revisao.id;

    // Carregar data da revisão
    if (revisao.dtRevisao) {
      const data = new Date(revisao.dtRevisao);
      const dataFormatada = data.toISOString().slice(0, 16); // Formato YYYY-MM-DDTHH:MM
      document.getElementById("dataAgendamento").value = dataFormatada;
    }

    // Scroll para o formulário
    document.getElementById("agendamentoForm").scrollIntoView({ behavior: "smooth" });
  } catch (erro) {
    alert(`Erro ao editar agendamento: ${erro.message}`);
  }
}
