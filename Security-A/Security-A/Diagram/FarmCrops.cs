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
    
    public partial class FarmCrops
    {
        public int Id { get; set; }
        public int CropId { get; set; }
        public int FarmId { get; set; }
        public int Num_hectareas { get; set; }
        public bool State { get; set; }
        public System.DateTime Created_at { get; set; }
        public Nullable<System.DateTime> Updated_at { get; set; }
        public Nullable<System.DateTime> Deleted_at { get; set; }
    
        public virtual Crops Crops { get; set; }
        public virtual Farms Farms { get; set; }
    }
}
