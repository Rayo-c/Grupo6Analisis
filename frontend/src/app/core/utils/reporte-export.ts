import { ReporteQuejasResponse } from '../models/reporte.model';

/**
 * CU15 - Exportacion del reporte a PDF y Excel (se genera 100% en el navegador).
 * Las librerias se cargan bajo demanda para no aumentar el bundle inicial.
 */

function marcaTiempo(): string {
    const d = new Date();
    const p = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}${p(d.getMonth() + 1)}${p(d.getDate())}-${p(d.getHours())}${p(d.getMinutes())}`;
}

const filas = (mapa: Record<string, number>): (string | number)[][] =>
    Object.entries(mapa).map(([k, v]) => [k, v]);

export async function exportarReporteExcel(r: ReporteQuejasResponse, filtros: string): Promise<void> {
    const XLSX = await import('xlsx');
    const libro = XLSX.utils.book_new();
    const hoja = (nombre: string, datos: (string | number)[][], anchos: number[]) => {
        const ws = XLSX.utils.aoa_to_sheet(datos);
        ws['!cols'] = anchos.map((wch) => ({ wch }));
        XLSX.utils.book_append_sheet(libro, ws, nombre);
    };

    hoja('Resumen', [
        ['Reporte de Quejas - QuickBite'],
        ['Generado', new Date().toLocaleString('es-GT')],
        ['Filtros', filtros],
        [],
        ['Indicador', 'Valor'],
        ['Total de quejas', r.totalQuejas],
        ['Tiempo promedio de resolucion (horas)', Number(r.tiempoPromedioResolucionHoras.toFixed(1))],
        ['Indice de satisfaccion promedio (1-5)', Number(r.indiceSatisfaccionPromedio.toFixed(1))]
    ], [40, 40]);
    hoja('Por Categoria', [['Categoria', 'Quejas'], ...filas(r.porCategoria)], [30, 12]);
    hoja('Por Sucursal', [['Sucursal', 'Quejas'], ...filas(r.porSucursal)], [30, 12]);
    hoja('Por Estado', [['Estado', 'Quejas'], ...filas(r.porEstado)], [30, 12]);
    hoja('Desempeno por Agente', [
        ['Agente', 'Quejas atendidas', 'Calificacion promedio'],
        ...r.desempenoPorAgente.map((a) => [a.agenteNombre, a.quejasAtendidas, Number(a.calificacionPromedio.toFixed(1))])
    ], [30, 18, 22]);

    XLSX.writeFile(libro, `reporte-quejas-${marcaTiempo()}.xlsx`);
}

export async function exportarReportePdf(r: ReporteQuejasResponse, filtros: string): Promise<void> {
    const { jsPDF } = await import('jspdf');
    const autoTable = (await import('jspdf-autotable')).default;

    const doc = new jsPDF();
    doc.setFontSize(16);
    doc.text('Reporte de Quejas - QuickBite', 14, 18);
    doc.setFontSize(9);
    doc.text(`Generado: ${new Date().toLocaleString('es-GT')}`, 14, 25);
    doc.text(`Filtros: ${filtros}`, 14, 30, { maxWidth: 180 });

    const tabla = (titulo: string, cabecera: string[], cuerpo: (string | number)[][]) => {
        const y = ((doc as any).lastAutoTable?.finalY ?? 34) + 8;
        doc.setFontSize(11);
        doc.text(titulo, 14, y);
        autoTable(doc, {
            startY: y + 2,
            head: [cabecera],
            body: cuerpo.length ? cuerpo : [['Sin datos', '']],
            styles: { fontSize: 9 },
            headStyles: { fillColor: [0, 48, 73] }
        });
    };

    tabla('Resumen', ['Indicador', 'Valor'], [
        ['Total de quejas', r.totalQuejas],
        ['Tiempo promedio de resolucion', `${r.tiempoPromedioResolucionHoras.toFixed(1)} horas`],
        ['Indice de satisfaccion promedio', `${r.indiceSatisfaccionPromedio.toFixed(1)} / 5`]
    ]);
    tabla('Quejas por Categoria', ['Categoria', 'Quejas'], filas(r.porCategoria));
    tabla('Quejas por Sucursal', ['Sucursal', 'Quejas'], filas(r.porSucursal));
    tabla('Quejas por Estado', ['Estado', 'Quejas'], filas(r.porEstado));
    tabla('Desempeno por Agente', ['Agente', 'Quejas atendidas', 'Calificacion promedio'],
        r.desempenoPorAgente.map((a) => [a.agenteNombre, a.quejasAtendidas, `${a.calificacionPromedio.toFixed(1)} / 5`]));

    doc.save(`reporte-quejas-${marcaTiempo()}.pdf`);
}