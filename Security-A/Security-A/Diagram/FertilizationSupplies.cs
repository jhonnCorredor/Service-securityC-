//------------------------------------------------------------------------------
// <auto-generated>
//     Este código se generó a partir de una plantilla.
//
//     Los cambios manuales en este archivo pueden causar un comportamiento inesperado de la aplicación.
//     Los cambios manuales en este archivo se sobrescribirán si se regenera el código.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Diagram
{
    using System;
    using System.Collections.Generic;
    
    public partial class FertilizationSupplies
    {
        public int Id { get; set; }
        public string Dose { get; set; }
        public int SuppliesId { get; set; }
        public int FertilizationId { get; set; }
        public bool State { get; set; }
        public System.DateTime Created_at { get; set; }
        public Nullable<System.DateTime> Updated_at { get; set; }
        public Nullable<System.DateTime> Deleted_at { get; set; }
    
        public virtual Fertilizations Fertilizations { get; set; }
        public virtual Supplies Supplies { get; set; }
    }
}
