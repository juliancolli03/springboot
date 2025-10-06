import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * =================================== SISTEMA DE GESTIÓN - TECHLAB ===================================
 * Archivo único: TechLabApp.java
 * Características:
 * - POO: Producto (abstracto), ProductoSimple (hereda), LineaPedido, Pedido
 * - Encapsulamiento con getters/setters validados
 * - Excepciones personalizadas: ValorInvalidoException, ProductoNoEncontradoException, StockInsuficienteException
 * - Colecciones: ArrayList
 * - Menú interactivo de consola con validaciones robustas
 * - ABM de Productos (alta/listado/búsqueda-actualización/eliminación con confirmación)
 * - Pedidos: creación (valida stock), cálculo total, descuento de stock al confirmar, listado de pedidos
 * =====================================================================================================
 */
public class TechLab {

    // ===================== Almacenamiento en memoria =====================
    private static final List<Producto> INVENTARIO = new ArrayList<>();
    private static final List<Pedido> PEDIDOS = new ArrayList<>();

    // ===================== Entrada estándar =====================
    private static final Scanner SC = new Scanner(System.in);

    public static void main(String[] args) {
        ejecutarMenuPrincipal();
    }

    // ===================== Menú principal =====================
    private static void ejecutarMenuPrincipal() {
        String opcion;
        do {
            imprimirEncabezado();
            System.out.println("1) Agregar producto");
            System.out.println("2) Listar productos");
            System.out.println("3) Buscar/Actualizar producto");
            System.out.println("4) Eliminar producto");
            System.out.println("5) Crear un pedido");
            System.out.println("6) Listar pedidos");
            System.out.println("7) Salir");
            System.out.print("\nElija una opción: ");
            opcion = SC.nextLine().trim();

            switch (opcion) {
                case "1": agregarProducto(); break;
                case "2": listarProductos(); break;
                case "3": buscarActualizarProducto(); break;
                case "4": eliminarProducto(); break;
                case "5": crearPedido(); break;
                case "6": listarPedidos(); break;
                case "7": System.out.println("\n¡Gracias por usar el sistema!"); break;
                default: System.out.println("\nOpción inválida. Intente nuevamente.");
            }

            if (!"7".equals(opcion)) {
                pausar();
            }
        } while (!"7".equals(opcion));
    }

    private static void imprimirEncabezado() {
        System.out.println("\n=================================== SISTEMA DE GESTIÓN - TECHLAB ==================================");
    }

    private static void pausar() {
        System.out.print("\nPresione ENTER para continuar...");
        SC.nextLine();
    }

    // ===================== Opción 1: Agregar producto =====================
    private static void agregarProducto() {
        System.out.println("\n--- Agregar producto ---");
        String nombre = leerNoVacio("Nombre del producto: ");
        double precio = leerDoublePositivo("Precio (ej. 199.99): ");
        int stock = leerEnteroNoNegativo("Cantidad en stock (entero >= 0): ");

        try {
            Producto nuevo = new ProductoSimple(nombre, precio, stock);
            INVENTARIO.add(nuevo);
            System.out.printf("✔ Producto agregado con ID %d%n", nuevo.getId());
        } catch (ValorInvalidoException e) {
            System.out.println("✖ Error al crear el producto: " + e.getMessage());
        }
    }

    // ===================== Opción 2: Listar productos =====================
    private static void listarProductos() {
        System.out.println("\n--- Listado de productos ---");
        if (INVENTARIO.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }
        System.out.printf("%-5s %-30s %-12s %-8s%n", "ID", "Nombre", "Precio", "Stock");
        System.out.println("--------------------------------------------------------------------------------");
        for (Producto p : INVENTARIO) {
            System.out.printf("%-5d %-30s $%-11.2f %-8d%n",
                    p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
        }
    }

    // ===================== Opción 3: Buscar / Actualizar =====================
    private static void buscarActualizarProducto() {
        System.out.println("\n--- Buscar/Actualizar producto ---");
        Producto p = buscarProductoInteractivo();
        if (p == null) return;

        mostrarProductoDetallado(p);

        System.out.print("\n¿Desea actualizar el producto? (s/n): ");
        String r = SC.nextLine().trim().toLowerCase(Locale.ROOT);
        if (r.equals("s") || r.equals("si") || r.equals("sí")) {
            actualizarProducto(p);
        }
    }

    private static void mostrarProductoDetallado(Producto p) {
        System.out.println("\n> Información del producto");
        System.out.println("ID:      " + p.getId());
        System.out.println("Nombre:  " + p.getNombre());
        System.out.printf("Precio:  $%.2f%n", p.getPrecio());
        System.out.println("Stock:   " + p.getStock());
        System.out.println("Tipo:    " + p.getClass().getSimpleName()); // polimorfismo simple
    }

    private static void actualizarProducto(Producto p) {
        System.out.println("\n¿Qué desea actualizar?");
        System.out.println("1) Precio");
        System.out.println("2) Stock");
        System.out.println("3) Cancelar");
        System.out.print("Opción: ");
        String op = SC.nextLine().trim();

        try {
            switch (op) {
                case "1": {
                    double nuevoPrecio = leerDoublePositivo("Nuevo precio: ");
                    p.setPrecio(nuevoPrecio);
                    System.out.println("✔ Precio actualizado.");
                    break;
                }
                case "2": {
                    int nuevoStock = leerEnteroNoNegativo("Nuevo stock: ");
                    p.setStock(nuevoStock);
                    System.out.println("✔ Stock actualizado.");
                    break;
                }
                case "3":
                    System.out.println("Operación cancelada.");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } catch (ValorInvalidoException e) {
            System.out.println("✖ Error de validación: " + e.getMessage());
        }
    }

    // ===================== Opción 4: Eliminar producto =====================
    private static void eliminarProducto() {
        System.out.println("\n--- Eliminar producto ---");
        Producto p = buscarProductoInteractivo();
        if (p == null) return;

        mostrarProductoDetallado(p);

        System.out.print("\n¿Confirma eliminar? (s/n): ");
        String r = SC.nextLine().trim().toLowerCase(Locale.ROOT);
        if (r.equals("s") || r.equals("si") || r.equals("sí")) {
            INVENTARIO.remove(p);
            System.out.println("✔ Producto eliminado.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    // ===================== Opción 5: Crear pedido =====================
    private static void crearPedido() {
        System.out.println("\n--- Crear pedido ---");

        if (INVENTARIO.isEmpty()) {
            System.out.println("No hay productos en inventario para crear un pedido.");
            return;
        }

        Pedido pedido = new Pedido();
        boolean agregando = true;

        while (agregando) {
            listarProductos();
            System.out.println("\nSeleccione producto por ID o nombre (vacío para terminar):");
            String entrada = SC.nextLine().trim();
            if (entrada.isEmpty()) break;

            Producto p = null;
            try {
                p = buscarProducto(entrada);
            } catch (ProductoNoEncontradoException e) {
                System.out.println("✖ " + e.getMessage());
                continue;
            }

            int cantidad = leerEnteroPositivo("Cantidad a agregar: ");

            try {
                // Validar stock disponible al momento de armar el pedido
                if (cantidad > p.getStock()) {
                    throw new StockInsuficienteException("Stock insuficiente. Disponible: " + p.getStock());
                }

                pedido.agregarLinea(new LineaPedido(p, cantidad));
                System.out.printf("✔ Agregado: %s x %d%n", p.getNombre(), cantidad);

            } catch (ValorInvalidoException | StockInsuficienteException e) {
                System.out.println("✖ " + e.getMessage());
            }

            System.out.print("\n¿Seguir agregando productos? (s/n): ");
            String r = SC.nextLine().trim().toLowerCase(Locale.ROOT);
            agregando = (r.equals("s") || r.equals("si") || r.equals("sí"));
        }

        if (pedido.getLineas().isEmpty()) {
            System.out.println("No se agregaron productos. Pedido cancelado.");
            return;
        }

        // Mostrar resumen
        System.out.println("\nResumen del pedido:");
        System.out.printf("%-5s %-30s %-10s %-10s %-10s%n", "ID", "Producto", "Precio", "Cant.", "Subtotal");
        System.out.println("--------------------------------------------------------------------------");
        for (LineaPedido l : pedido.getLineas()) {
            System.out.printf("%-5d %-30s $%-9.2f %-10d $%-9.2f%n",
                    l.getProducto().getId(), l.getProducto().getNombre(), l.getProducto().getPrecio(),
                    l.getCantidad(), l.getSubtotal());
        }
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("TOTAL: $%.2f%n", pedido.calcularTotal());

        System.out.print("\n¿Confirmar pedido y descontar stock? (s/n): ");
        String conf = SC.nextLine().trim().toLowerCase(Locale.ROOT);
        if (conf.equals("s") || conf.equals("si") || conf.equals("sí")) {
            try {
                // Verifica nuevamente y descuenta
                pedido.confirmar();
                PEDIDOS.add(pedido);
                System.out.printf("✔ Pedido confirmado con ID %d. Fecha: %s%n", pedido.getId(),
                        pedido.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } catch (StockInsuficienteException e) {
                System.out.println("✖ No se pudo confirmar el pedido: " + e.getMessage());
            }
        } else {
            System.out.println("Pedido cancelado.");
        }
    }

    // ===================== Opción 6: Listar pedidos =====================
    private static void listarPedidos() {
        System.out.println("\n--- Listado de pedidos ---");
        if (PEDIDOS.isEmpty()) {
            System.out.println("No hay pedidos realizados.");
            return;
        }

        for (Pedido ped : PEDIDOS) {
            System.out.println("\n------------------------------------------");
            System.out.printf("Pedido ID: %d | Fecha: %s%n", ped.getId(),
                    ped.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            System.out.printf("%-5s %-30s %-10s %-10s %-10s%n", "ID", "Producto", "Precio", "Cant.", "Subtotal");
            for (LineaPedido l : ped.getLineas()) {
                System.out.printf("%-5d %-30s $%-9.2f %-10d $%-9.2f%n",
                        l.getProducto().getId(), l.getProducto().getNombre(), l.getProducto().getPrecio(),
                        l.getCantidad(), l.getSubtotal());
            }
            System.out.printf("TOTAL: $%.2f%n", ped.calcularTotal());
            System.out.println("------------------------------------------");
        }
    }

    // ===================== Utilidades de entrada =====================
    private static String leerNoVacio(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("El valor no puede estar vacío.");
        }
    }

    private static double leerDoublePositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim().replace(",", ".");
            try {
                double v = Double.parseDouble(s);
                if (v <= 0) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número decimal válido (> 0).");
            }
        }
    }

    private static int leerEnteroNoNegativo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < 0) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un entero válido (>= 0).");
            }
        }
    }

    private static int leerEnteroPositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v <= 0) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un entero válido (> 0).");
            }
        }
    }

    // ===================== Búsqueda de producto (por ID o nombre) =====================
    private static Producto buscarProductoInteractivo() {
        String criterio = leerNoVacio("Ingrese ID o nombre del producto: ");
        try {
            Producto p = buscarProducto(criterio);
            System.out.println("✔ Producto encontrado.");
            return p;
        } catch (ProductoNoEncontradoException e) {
            System.out.println("✖ " + e.getMessage());
            return null;
        }
    }

    private static Producto buscarProducto(String criterio) throws ProductoNoEncontradoException {
        // Intentar por ID
        try {
            int id = Integer.parseInt(criterio);
            for (Producto p : INVENTARIO) {
                if (p.getId() == id) return p;
            }
        } catch (NumberFormatException ignored) {
            // No era un número, buscar por nombre (case-insensitive, contiene)
        }

        String critLower = criterio.toLowerCase(Locale.ROOT);
        List<Producto> coincidencias = new ArrayList<>();
        for (Producto p : INVENTARIO) {
            if (p.getNombre().toLowerCase(Locale.ROOT).contains(critLower)) {
                coincidencias.add(p);
            }
        }

        if (coincidencias.isEmpty()) {
            throw new ProductoNoEncontradoException("No se encontró un producto para: " + criterio);
        } else if (coincidencias.size() == 1) {
            return coincidencias.get(0);
        } else {
            System.out.println("\nSe encontraron varias coincidencias:");
            System.out.printf("%-5s %-30s %-12s %-8s%n", "ID", "Nombre", "Precio", "Stock");
            for (Producto p : coincidencias) {
                System.out.printf("%-5d %-30s $%-11.2f %-8d%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }
            int idElegido = leerEnteroPositivo("Ingrese el ID exacto a seleccionar: ");
            for (Producto p : coincidencias) {
                if (p.getId() == idElegido) return p;
            }
            throw new ProductoNoEncontradoException("El ID ingresado no coincide con la lista mostrada.");
        }
    }
}

/* ===================== Dominio y Excepciones (mismo archivo) ===================== */

// POO: Clase abstracta base (permite extender a otros tipos de productos si se desea)
abstract class Producto {
    private static int SEQ = 1;

    private final int id;
    private String nombre;
    private double precio;
    private int stock;

    protected Producto(String nombre, double precio, int stock) throws ValorInvalidoException {
        this.id = SEQ++;
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) throws ValorInvalidoException {
        if (nombre == null || nombre.trim().isEmpty())
            throw new ValorInvalidoException("El nombre no puede estar vacío.");
        this.nombre = nombre.trim();
    }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) throws ValorInvalidoException {
        if (precio <= 0)
            throw new ValorInvalidoException("El precio debe ser mayor a 0.");
        this.precio = precio;
    }

    public int getStock() { return stock; }
    public void setStock(int stock) throws ValorInvalidoException {
        if (stock < 0)
            throw new ValorInvalidoException("El stock no puede ser negativo.");
        this.stock = stock;
    }

    public void descontarStock(int cantidad) throws StockInsuficienteException, ValorInvalidoException {
        if (cantidad <= 0) throw new ValorInvalidoException("La cantidad a descontar debe ser > 0.");
        if (cantidad > stock) throw new StockInsuficienteException("Stock insuficiente. Disponible: " + stock);
        this.stock -= cantidad;
    }
}

// Implementación concreta: Producto simple (polimorfismo básico)
class ProductoSimple extends Producto {
    public ProductoSimple(String nombre, double precio, int stock) throws ValorInvalidoException {
        super(nombre, precio, stock);
    }
}

// Línea de pedido con referencia a producto y cantidad
class LineaPedido {
    private final Producto producto;
    private final int cantidad;

    public LineaPedido(Producto producto, int cantidad) throws ValorInvalidoException {
        if (producto == null) throw new ValorInvalidoException("La línea debe tener un producto.");
        if (cantidad <= 0) throw new ValorInvalidoException("La cantidad debe ser > 0.");
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}

// Pedido con lista de líneas y confirmación (descuenta stock)
class Pedido {
    private static int SEQ = 1;
    private final int id;
    private final List<LineaPedido> lineas = new ArrayList<>();
    private final LocalDateTime fecha;

    public Pedido() {
        this.id = SEQ++;
        this.fecha = LocalDateTime.now();
    }

    public int getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public List<LineaPedido> getLineas() { return Collections.unmodifiableList(lineas); }

    public void agregarLinea(LineaPedido l) {
        lineas.add(l);
    }

    public double calcularTotal() {
        double total = 0.0;
        for (LineaPedido l : lineas) total += l.getSubtotal();
        return total;
    }

    // Vuelve a validar stock y descuenta; operación "atómica" a nivel lógico
    public void confirmar() throws StockInsuficienteException {
        // Verificación previa
        for (LineaPedido l : lineas) {
            if (l.getCantidad() > l.getProducto().getStock()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para producto '" + l.getProducto().getNombre() +
                                "'. Disponible: " + l.getProducto().getStock() + ", requerido: " + l.getCantidad());
            }
        }
        // Descuento efectivo
        for (LineaPedido l : lineas) {
            try {
                l.getProducto().descontarStock(l.getCantidad());
            } catch (ValorInvalidoException e) {
                // No debería ocurrir por validación previa, pero lo reportamos
                throw new RuntimeException("Error inesperado al descontar stock: " + e.getMessage(), e);
            }
        }
    }
}

// Excepciones personalizadas
class ValorInvalidoException extends Exception {
    public ValorInvalidoException(String msg) { super(msg); }
}

class ProductoNoEncontradoException extends Exception {
    public ProductoNoEncontradoException(String msg) { super(msg); }
}

class StockInsuficienteException extends Exception {
    public StockInsuficienteException(String msg) { super(msg); }
}
