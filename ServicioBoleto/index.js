const express =require('express')
const mysql =require('mysql')
const bodyParser = require('body-parser')

const app = express()
app.use(bodyParser.json())

const PUERTO = 4000

const conexion = mysql.createConnection(
    {
        host:'localhost',
        database: 'boletos',
        user:'root',
        password: 'marianito1'
    }
)

app.listen(PUERTO, ()=>{
    console.log("Servidor corriendo en el puerto "+PUERTO)
})

conexion.connect(error =>{
    if(error) throw error
    console.log("Conexión exitosa a la base de datos")
})
app.get("/", (req, res) =>{
    res.send("Servicio Web funcionando")
})
/*-----------------------------USUARIO------------------------------------------*/
app.get("/usuario", (req, res) =>{
    const query = "SELECT * FROM usuario;"
    conexion.query(query, (error, resultado) =>{
        if(error) return console.error(error.message)

        const objeto ={}
        if(resultado.length > 0){
            objeto.listaUsuarios = resultado
            res.json(objeto)
        }else{
            res.json("No hay registros")
        }
    })
})
app.post("/usuario/agregar", (req, res) =>{
    const usuario = {
        usuNombre: req.body.usuNombre,
        usuApellido: req.body.usuApellido,
        usuCorreo: req.body.usuCorreo,
        usuPassword: req.body.usuPassword,
        usuEdad: req.body.usuEdad
    }
    const query = "INSERT INTO usuario SET ?"
    conexion.query(query, usuario, (error) =>{
        if(error) return console.error(error.message)

        res.json("Se inserto correctamente el usuario")
    })
})
app.put("/usuario/actualizar/:id", (req, res) => {
    const {id} = req.params;
    const {usuNombre, usuApellido, usuCorreo, usuPassword, usuEdad} = req.body;

    const query = "UPDATE usuario SET usuNombre = ?, usuApellido = ?, usuCorreo = ?, usuPassword = ?, usuEdad = ? WHERE usuId = ?";
    const values = [usuNombre, usuApellido, usuCorreo, usuPassword, usuEdad, id];
    console.log(values)
    conexion.query(query, values, (error) => {
        if (error) {
            console.error(error.message);
            return res.status(500).json({ error: "Error al actualizar el usuario" });
        }
        res.json("Se actualizó correctamente el usuario");
    });
});
app.get("/usuario/:id", (req, res) => {
    const { id } = req.params;
    const query = "SELECT * FROM usuario WHERE usuId = ?";
    //console.log(query)
    conexion.query(query, [id], (error, resultado) => {
        if (error) {
            console.error(error.message);
            return res.status(500).json("Error en el servidor");
        }

        if (resultado.length > 0) {
            res.json(resultado[0]); // Devolver solo el primer resultado encontrado

            
        } else {
            res.status(404).json("Usuario no encontrado");
        }
    });
});

app.post("/usuario/login", (req, res) => {
    const { usuario, contrasena } = req.query;
    const query = "SELECT usuId FROM usuario WHERE usuCorreo = ? AND usuPassword = ?";
    conexion.query(query, [usuario, contrasena], (error, resultado) => {
        if (error) {
            console.error(error.message);
            return res.status(500).json("Error en el servidor");
            
        }

        if (resultado.length > 0) {
            const { usuId } = resultado[0];
            //console.log({ usuId })
            res.json({ usuId });  // Devuelve el usuId en formato JSON
        } else {
            res.status(401).json("Credenciales incorrectas");  // Enviar un error si las credenciales son incorrectas
        }
    });
});
/*-----------------------------VIAJE------------------------------------------*/

app.get("/viaje", (req, res) =>{
    const query = "SELECT * FROM viaje;"
    conexion.query(query, (error, resultado) =>{
        if(error) return console.error(error.message)

        const objeto ={}
        if(resultado.length > 0){
            objeto.listaViaje = resultado
            res.json(objeto)
        }else{
            res.json("No hay registros")
        }
    })
})
app.get("/viaje/:usuId", (req, res) => {
    const usuId = req.params.usuId;
    const query = "SELECT * FROM viaje WHERE usuId = ?";

    conexion.query(query, [usuId], (error, resultado) => {
        if (error) return console.error(error.message);

        const objeto = {};
        if (resultado.length > 0) {
            objeto.listaViaje = resultado;
            res.json(objeto);
        } else {
            res.json("No hay registros");
        }
    });
});

app.post("/viaje/agregar", (req, res) => {
    const viaje = {
        viajeOrigen: req.body.viajeOrigen,
        viajeDestino: req.body.viajeDestino,
        viajeFecha: req.body.viajeFecha,
        usuId: req.body.usuId,
        precio: req.body.precio
    };

    const queryUsuario = "SELECT * FROM usuario WHERE usuId = ?";

    // Verificar si el usuId existe en la tabla usuario
    conexion.query(queryUsuario, [viaje.usuId], (error, resultado) => {
        if (error) {
            console.error(error.message);
            res.status(500).json("Error al verificar el usuario");
            return;
        }

        if (resultado.length === 0) {
            res.status(400).json("Usuario no existe");
            return;
        }

        const queryViaje = "INSERT INTO viaje SET ?";
        conexion.query(queryViaje, viaje, error => {
            if (error) {
                console.error(error.message);
                res.status(500).json("Error al insertar el viaje");
                return;
            }

            res.json("Se registró correctamente el pago");
        });
    });
});

app.put("/viaje/actualizar/:id", (req, res) => {
    const { id } = req.params;
    const viaje = {
        viajeOrigen: req.body.viajeOrigen,
        viajeDestino: req.body.viajeDestino,
        viajeFecha: req.body.viajeFecha,
        usuId: req.body.usuId,  
        precio: req.body.precio
    }
    const query = "UPDATE viaje SET ? WHERE idViaje = ?";
    conexion.query(query, [viaje, id], (error) => {
        if (error) {
            console.error(error.message);
            res.status(500).json("Error al actualizar el viaje");
            return;
        }

        res.json("Viaje actualizado correctamente");
    });
});
/*app.get("/viaje/:id", (req, res) => {
    const { id } = req.params;
    const query = "SELECT * FROM viaje WHERE idViaje = ?";
    console.log(query)
    conexion.query(query, [id], (error, resultado) => {
        if (error) {
            console.error(error.message);
            return res.status(500).json("Error en el servidor");
        }

        if (resultado.length > 0) {
            res.json(resultado[0]); // Devolver solo el primer resultado encontrado
            

            
        } else {
            res.status(404).json("Viaje no encontrado");
        }
    });
});*/
app.get("/viaje/ultimo/:usuId", (req, res) => {
    const usuId = req.params.usuId;
    const query = "SELECT * FROM viaje WHERE usuId = ? ORDER BY idViaje DESC LIMIT 1";

    conexion.query(query, [usuId], (error, resultado) => {
        if (error) {
            console.error(error.message);
            res.status(500).json("Error al obtener el viaje");
            return;
        }

        if (resultado.length === 0) {
            res.status(404).json("No se encontraron viajes para este usuario");
            return;
        }

        res.json(resultado[0]);
    });
});



/*-----------------------------DETALLE------------------------------------------*/

app.get("/detalle", (req, res) => {
    const query = "SELECT * FROM detalle;";
    conexion.query(query, (error, resultado) => {
        if (error) {
            console.error(error.message);
            res.status(500).json("Error al obtener los detalles");
            return;
        }

        if (resultado.length > 0) {
            const detalle = resultado[0]; // Obtener el primer (y único) registro
            res.json(detalle); // Devolver un solo objeto Detalle
        } else {
            res.status(404).json("No hay registros en la tabla detalle");
        }
    });
});


/*-----------------------------PAGO------------------------------------------*/
app.get("/pago", (req, res) =>{
    const query = "SELECT * FROM pago;"
    conexion.query(query, (error, resultado) =>{
        if(error) return console.error(error.message)

        const objeto ={}
        if(resultado.length > 0){
            objeto.listaPago = resultado
            res.json(objeto)
        }else{
            res.json("No hay registros")
        }
    })
})
app.post("/pago/agregar", (req, res) => {
    const pago = {
        pagoNombre: req.body.pagoNombre,
        pagoTargeta: req.body.pagoTargeta,
        pagoCodigo: req.body.pagoCodigo,
        pagoVencimiento: req.body.pagoVencimiento,
        usuId: req.body.usuId,
    };

    const queryUsuario = "SELECT * FROM usuario WHERE usuId = ?";

    // Verificar si el usuId existe en la tabla usuario
    conexion.query(queryUsuario, [pago.usuId], (error, resultado) => {
        if (error) {
            console.error(error.message);
            res.status(500).json("Error al verificar el usuario");
            return;
        }

        if (resultado.length === 0) {
            res.status(400).json("Usuario no existe");
            return;
        }

        const queryPago = "INSERT INTO pago SET ?";
        conexion.query(queryPago, pago, error => {
            if (error) {
                console.error(error.message);
                res.status(500).json("Error al registrar el pago");
                return;
            }

            res.json("Se registró correctamente el pago");
        });
    });
});
app.get("/pago/ultimo/:usuId", (req, res) => {
    const usuId = req.params.usuId;
    const query = "SELECT * FROM pago WHERE usuId = ? ORDER BY idPago DESC LIMIT 1";

    conexion.query(query, [usuId], (error, resultado) => {
        if (error) {
            console.error(error.message);
            res.status(500).json("Error al obtener el pago");
            return;
        }

        if (resultado.length === 0) {
            res.status(404).json("No se encontraron pagos para este usuario");
            return;
        }

        res.json(resultado[0]);
    });
});

// Obtener pagos de un viaje específico
app.get("/pago/viaje/:idViaje", (req, res) => {
    const { idViaje } = req.params;
    const query = "SELECT * FROM pago WHERE idViaje = ?;";
    conexion.query(query, [idViaje], (error, resultado) => {
        if (error) return console.error(error.message);

        const objeto = {};
        if (resultado.length > 0) {
            objeto.listaPago = resultado;
            res.json(objeto);
        } else {
            res.json("No hay registros de pagos para este viaje");
        }
    });
});

app.get("/historial", (req, res) => {
    const usuId = req.query.usuId; // Recibir el usuId desde la autenticación

    const query = `
        SELECT
            u.usuId,
            u.usuNombre,
            u.usuApellido,
            v.viajeOrigen,
            v.viajeDestino,
            v.viajeFecha
        FROM
            usuario u
            JOIN viaje v ON u.usuId = v.usuId
        WHERE
            u.usuId = ?;
    `;
    conexion.query(query, [usuId], (error, resultado) => {
        if (error) return console.error(error.message);

        const objeto = {};
        if (resultado.length > 0) {
            objeto.listaHistorial = resultado;
            res.json(objeto);
        } else {
            res.json("No hay registros en el historial para este usuario");
        }
    });
});





