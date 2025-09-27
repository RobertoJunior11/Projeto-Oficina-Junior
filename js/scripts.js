// URLs das APIs
const PROPRIETARIO_API_URL = "http://localhost:8080/proprietario";
const VEICULO_API_URL = "http://localhost:8080/veiculo";

// Variáveis de paginação
let paginaProprietarios = 0;
let paginaVeiculos = 0;

// Contador para IDs únicos dos veículos no formulário
let veiculoCounter = 0;

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

// Inicialização
carregarProprietarios();
